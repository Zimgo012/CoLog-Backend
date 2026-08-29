package com.zimgo.colog.revision;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevisionRepository extends JpaRepository<Revision, Long> {

    List<Revision> findAllByDocumentDocumentId(Long documentId);

}
