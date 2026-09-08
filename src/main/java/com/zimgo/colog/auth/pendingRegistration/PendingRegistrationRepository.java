package com.zimgo.colog.auth.pendingRegistration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

    PendingRegistration findByEmail(String email);

}
