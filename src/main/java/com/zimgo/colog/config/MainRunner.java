package com.zimgo.colog.config;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryRepository;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.document.DocumentRepository;
import com.zimgo.colog.document.DocumentService;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRepository;
import com.zimgo.colog.user.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MainRunner {

    private final DocumentService documentService;
    private final PasswordEncoder passwordEncoder;
    public MainRunner(DocumentService documentService,
    PasswordEncoder passwordEncoder) {
        this.documentService = documentService;
        this.passwordEncoder = passwordEncoder;

    }

    @Component
    @Order(1)
    @Transactional
    class UserSeeder implements CommandLineRunner {

        public UserRepository userRepository;
        public DocumentRepository documentRepository;
        public DiaryRepository diaryRepository;

        public UserSeeder(UserRepository userRepository, DocumentRepository documentRepository, DiaryRepository diaryRepository) {
            this.userRepository = userRepository;
            this.documentRepository = documentRepository;
            this.diaryRepository = diaryRepository;
        }

        @Override
        public void run(String... args) throws Exception {

            System.out.println("USER - Seeding Data");

            // ---------------- USERS ----------------
            User john = new User(null, "John", "Doe","jdoe223", "jdoe@mail.com", passwordEncoder.encode("john123"), UserRole.USER,
                    new ArrayList<>(), new ArrayList<>());

            User ellen = new User(null, "Ellen", "Green","egreen01", "egreen@mail.com","",UserRole.ADMIN,
                    new ArrayList<>(), new ArrayList<>());

            User jaden = new User(null, "Jaden", "Vance","jvance69", "jvance@mail.com","",UserRole.USER,
                    new ArrayList<>(), new ArrayList<>());

            User beth = new User(null, "Beth", "Holland", "bholland67","bholland@mail.com","",UserRole.ADMIN,
                    new ArrayList<>(), new ArrayList<>());

            userRepository.saveAll(List.of(john, ellen, jaden, beth));

            // ---------------- DIARIES ----------------
            Diary diary1 = new Diary(
                    null,
                    "diary1",
                    "",
                    true,
                    LocalDate.now(),
                    LocalDate.now(),
                    beth,
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            Diary diary2 = new Diary(
                    null,
                    "diary2",
                    "",
                    false,
                    LocalDate.now(),
                    LocalDate.now(),
                    beth,
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            diaryRepository.saveAll(List.of(diary1, diary2));

            // link ownership AFTER persistence
            beth.getOwnedDiary().add(diary1);
            beth.getOwnedDiary().add(diary2);

            jaden.getCollaboratedDiary().add(diary1);
            jaden.getCollaboratedDiary().add(diary2);

            userRepository.saveAll(List.of(beth, jaden));

            // ---------------- DOCUMENTS ----------------
            Document doc1 = new Document();
            doc1.setDate(LocalDateTime.now());
//            doc1.setContent("Initial document content");
            doc1.setDiary(diary1);

            // IMPORTANT:
            // This creates:
            // - Document
            // - INITIAL DeltaLog (revision-1.txt)
            documentService.createDocument(doc1);

            System.out.println("USER - Seeding Data Finished");
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
                    new Diary(null, "diary3", "" ,  true, LocalDate.now(), LocalDate.now(),null,new ArrayList<>(),new ArrayList<>()),
                    new Diary(null, "diary4", "" ,  true, LocalDate.now(), LocalDate.now(), null,new ArrayList<>(),new ArrayList<>())

            ));
//            System.out.println("DIARY - Seeding Data Finished");
        }
    }
}
