package com.zimgo.colog.chat;

import com.zimgo.colog.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByDiaryOrderByTimestampAsc(Diary diary);
}
