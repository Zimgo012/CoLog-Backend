package com.zimgo.colog.config;

import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainRunner {

    @Component
    @Order(1)
    @Transactional
    class UserSeeder implements CommandLineRunner {

        public UserRepository userRepository;

        public UserSeeder(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public void run(String... args) throws Exception {
            System.out.println("USER - Seeding Data");

            userRepository.saveAll(List.of(
                    new User(null, "John", "Doe", "jdoe@mail.com"),
                    new User(null, "Ellen", "Green", "egreen@mail.com"),
                    new User(null, "Jaden", "Vance", "jvance@mail.com"),
                    new User(null, "Beth", "Holland", "bholland@mail.com")

            ));

            System.out.println("USER - Seeding Data Finished");


        }
    }

    @Component
    @Order(2)
    class SecondRunner implements CommandLineRunner {
        @Override
        public void run(String... args) throws Exception {
            System.out.println("Second Runner");
        }
    }
}
