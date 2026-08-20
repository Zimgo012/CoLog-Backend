package com.zimgo.colog.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {

    String firstName;

    String lastName;

    String username;

    String email;

    String password;


}