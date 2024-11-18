package dev.rm.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dev.rm.model.Role;
import dev.rm.model.User;
import dev.rm.repository.UserRepository;
import dev.rm.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        User adminUser = User.builder()
                .username("James")
                .email("james@mail.com")
                .password(PasswordUtil.hashPassword("123ABC"))
                .role(Role.ADMIN)
                .build();

        User regularUser = User.builder()
                .username("hank")
                .email("hank@mail.com")
                .password(PasswordUtil.hashPassword("123ABC"))
                .role(Role.USER)
                .build();

        userRepository.saveAll(Arrays.asList(adminUser, regularUser));
    }
}
