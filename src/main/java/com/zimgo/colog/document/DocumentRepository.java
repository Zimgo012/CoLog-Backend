package com.zimgo.colog.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

//    @Query("SELECT d FROM Document d WHERE d.diary.diaryId = :diaryId AND d.documentId = :id")

    @Query("""
    SELECT d
    FROM Document d
    WHERE d.diary.diaryId = :diaryId
    AND d.documentId = :id
    """)
    Optional<Document> findDocumentByDiaryIdAndDocumentId(@Param("diaryId") Long diaryId, @Param("id") Long id);

    @Query("""
    SELECT d
    FROM Document d
    WHERE d.diary.diaryId = :diaryId
    """)
    List<Document> findAllDocumentsByDiaryId(
            @Param("diaryId") Long diaryId
    );


}
