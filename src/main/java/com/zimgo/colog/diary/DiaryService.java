package com.zimgo.colog.diary;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaryService {
    public DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    //GET diary by id
    public Diary getDiary(Long id) {

        if (!diaryRepository.existsById(id)){
            throw new RuntimeException("Diary does not exist!");
        }

        return diaryRepository.findById(id).get();
    }

    //GET all diary
    public List<Diary> getAllDiary() {
        return diaryRepository.findAll();

    }

    //CREATE diary
    public Diary addDiary(Diary diary) {
        return diaryRepository.save(diary);
    }

    //DELETE diary
    public void deleteDiary(Long id) {
        diaryRepository.deleteById(id);
    }



}
