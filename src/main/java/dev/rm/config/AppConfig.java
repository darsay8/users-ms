package dev.rm.config;

import dev.rm.validation.EmailValidationHandler;
import dev.rm.validation.PasswordValidationHandler;
import dev.rm.validation.RoleValidationHandler;
import dev.rm.validation.ValidationChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public ValidationChain validationChain() {
        return new ValidationChain(Arrays.asList(
                new EmailValidationHandler(),
                new PasswordValidationHandler(),
                new RoleValidationHandler()));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Content-Type", "Authorization")
                .allowCredentials(true);
    }

}
