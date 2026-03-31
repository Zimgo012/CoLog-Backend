package com.zimgo.colog.config;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryRepository;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
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

//            System.out.println("USER - Seeding Data Finished");


        }
    }

    @Component
    @Order(2)
    class DiaryRunner implements CommandLineRunner {
        public DiaryRepository diaryRepository;
        public DiaryRunner(DiaryRepository diaryRepository) {
            this.diaryRepository = diaryRepository;
        }
        @Override
        public void run(String... args) throws Exception {
            System.out.println("DIARY - Seeding Data");
            diaryRepository.saveAll(List.of(
                    new Diary(null,"diary1",true, LocalDate.now(), LocalDate.now()),
                    new Diary(null, "diary2", true, LocalDate.now(), LocalDate.now()),
                    new Diary(null, "diary3", true, LocalDate.now(), LocalDate.now()),
                    new Diary(null, "diary4", true, LocalDate.now(), LocalDate.now())

            ));
//            System.out.println("DIARY - Seeding Data Finished");
        }
    }
}
