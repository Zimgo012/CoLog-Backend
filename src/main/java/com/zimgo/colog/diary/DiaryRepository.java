package com.zimgo.colog.diary;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findAllByOwnerUserId(Long userId);

    List<Diary> findAllByCollaboratorsUserId(Long userId);

}
