package com.zimgo.colog.diary;


import com.zimgo.colog.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findAllByOwnerUserId(Long userId);

    List<Diary> findAllByCollaboratorsUserId(Long userId);

    @Query("""
        SELECT u
        FROM Diary d
        JOIN d.collaborators u
        WHERE d.diaryId = :diaryId
          AND u.userId <> d.owner.userId
    """)
    List<User> findCollaboratorsExceptOwner(@Param("diaryId") Long diaryId);
}
