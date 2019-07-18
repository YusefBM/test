package com.github.dperezcabrera.test.documentation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
@Profile("swagger")
public class SwaggerConfig {

}
