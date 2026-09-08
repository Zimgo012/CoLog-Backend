package com.zimgo.colog.auth.pendingRegistration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class PendingRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String passwordHash;

    @Column
    private String verificationCodeHash;

    @Column
    private LocalDateTime expiresAt;

    @Column
    private LocalDateTime createdAt;
}


