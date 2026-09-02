package com.zimgo.colog.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    Long id;
    String token;
    String email;
    String username;
    String firstName;
    String lastName;

}
