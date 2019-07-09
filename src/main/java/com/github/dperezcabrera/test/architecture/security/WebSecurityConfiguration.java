package com.github.dperezcabrera.test.architecture.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@AllArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String XSRF_TOKEN = "XSRF-TOKEN";
    private static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
    private static final String[] AUTH_WHITELIST = {
        "/",
        "/auth/**",
        "/*.html",
        "/**/*.html",
        "/**/*.css",
        "/**/*.js",
        "/**/*.jpg",
        "/**/*.gif",
        "/**/*.mp4",
        "/**/*.png",
        "/webjars/**",
        // -- swagger ui
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/v2/api-docs"
    };

    private final SessionRegistry sessionRegistry;

    private final SimpleAuthenticationProvider authenticationProvider;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().disable();
        http.headers().httpStrictTransportSecurity().disable();
        http.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry);
        http.headers().frameOptions().deny();
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).denyAll()
                .antMatchers(HttpMethod.TRACE).denyAll()
                .antMatchers(HttpMethod.HEAD).denyAll()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .logout().logoutUrl("/auth/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID", XSRF_TOKEN).logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)).and()
                .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .csrf()
                .ignoringAntMatchers("/auth/**")
                .csrfTokenRepository(csrfTokenRepository());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(X_XSRF_TOKEN);
        return repository;
    }

    public class CsrfHeaderFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrf != null) {
                Cookie cookie = WebUtils.getCookie(request, XSRF_TOKEN);
                String token = csrf.getToken();
                if (cookie == null || (token != null && !token.equals(cookie.getValue()))) {
                    cookie = new Cookie(XSRF_TOKEN, token);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
