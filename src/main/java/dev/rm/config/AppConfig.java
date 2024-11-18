package dev.rm.config;

import dev.rm.validation.EmailValidationHandler;
import dev.rm.validation.PasswordValidationHandler;
import dev.rm.validation.RoleValidationHandler;
import dev.rm.validation.ValidationChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class AppConfig {

    @Bean
    public ValidationChain validationChain() {
        return new ValidationChain(Arrays.asList(
                new EmailValidationHandler(),
                new PasswordValidationHandler(),
                new RoleValidationHandler()));
    }
}
