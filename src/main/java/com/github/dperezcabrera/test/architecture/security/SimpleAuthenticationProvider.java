package com.github.dperezcabrera.test.architecture.security;

import com.github.dperezcabrera.test.architecture.auth.services.CredentialService;
import com.github.dperezcabrera.test.architecture.logging.Logging;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SimpleAuthenticationProvider implements AuthenticationProvider {

	private CredentialService credentialService;
	
    @Logging(disabled = true)
    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        if (credentialService.check(username, password)) {
            return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
        } else {
            throw new BadCredentialsException("Wrong credentials");
        }
    }

    @Logging(disabled = true)
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
