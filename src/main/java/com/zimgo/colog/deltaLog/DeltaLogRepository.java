package com.zimgo.colog.deltaLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeltaLogRepository  extends JpaRepository<DeltaLog,Long> {

    DeltaLog findTopByDocumentDocumentIdOrderByCreatedAtDesc(Long id);
    DeltaLog findTopByDocumentDocumentIdOrderByDeltaLogIdDesc(Long id);
}
