package com.zimgo.colog.config;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryRepository;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
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

            List<Diary> diaryList = new ArrayList<>();
            List<User> userList = new ArrayList<>();

            User john = new User(null, "John", "Doe", "jdoe@mail.com", new ArrayList<>(), new ArrayList<>());
            User ellen = new User(null, "Ellen", "Green", "egreen@mail.com", new ArrayList<>(),new ArrayList<>());
            User jaden = new User(null, "Jaden", "Vance", "jvance@mail.com", new ArrayList<>(), new ArrayList<>());
            User beth = new User(null, "Beth", "Holland", "bholland@mail.com", new ArrayList<>(), new ArrayList<>());

            Diary diary1 = new Diary(null, "diary1", "", new ArrayList<>(), true, LocalDate.now(), LocalDate.now(), beth,new ArrayList<>());
            Diary diary2 = new Diary(null, "diary2", "", new ArrayList<>(), false, LocalDate.now(), LocalDate.now(), beth,new ArrayList<>());

            jaden.getCollaboratedDiary().add(diary1);
            jaden.getCollaboratedDiary().add(diary2);

            beth.getOwnedDiary().add(diary1);
            beth.getOwnedDiary().add(diary2);

            userRepository.saveAll(List.of( john, ellen, jaden, beth ));

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
                    new Diary(null, "diary3", "" , new ArrayList<>(), true, LocalDate.now(), LocalDate.now(),null,new ArrayList<>()),
                    new Diary(null, "diary4", "" , new ArrayList<>(), true, LocalDate.now(), LocalDate.now(), null,new ArrayList<>())

            ));
//            System.out.println("DIARY - Seeding Data Finished");
        }
    }
}
