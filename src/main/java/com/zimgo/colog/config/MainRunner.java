package com.zimgo.colog.config;

import com.zimgo.colog.deltaLog.DeltaLogService;
import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryRepository;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.document.DocumentRepository;
import com.zimgo.colog.document.DocumentService;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class MainRunner {

    private final DeltaLogService deltaLogService;
    private final DocumentService documentService;

    public MainRunner(DeltaLogService deltaLogService, DocumentService documentService) {
        this.deltaLogService = deltaLogService;
        this.documentService = documentService;
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
            User john = new User(null, "John", "Doe", "jdoe@mail.com",
                    new ArrayList<>(), new ArrayList<>());

            User ellen = new User(null, "Ellen", "Green", "egreen@mail.com",
                    new ArrayList<>(), new ArrayList<>());

            User jaden = new User(null, "Jaden", "Vance", "jvance@mail.com",
                    new ArrayList<>(), new ArrayList<>());

            User beth = new User(null, "Beth", "Holland", "bholland@mail.com",
                    new ArrayList<>(), new ArrayList<>());

            userRepository.saveAll(List.of(john, ellen, jaden, beth));

            // ---------------- DIARIES ----------------
            Diary diary1 = new Diary(
                    null,
                    "diary1",
                    "",
                    new ArrayList<>(),
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
                    new ArrayList<>(),
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
                    new Diary(null, "diary3", "" , new ArrayList<>(), true, LocalDate.now(), LocalDate.now(),null,new ArrayList<>(),new ArrayList<>()),
                    new Diary(null, "diary4", "" , new ArrayList<>(), true, LocalDate.now(), LocalDate.now(), null,new ArrayList<>(),new ArrayList<>())

            ));
//            System.out.println("DIARY - Seeding Data Finished");
        }
    }
}
