package com.zimgo.colog.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequest {

    @Pattern(regexp = ".*\\S.*", message = "First name must not be blank")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    public String firstName;

    @Pattern(regexp = ".*\\S.*", message = "Last name must not be blank")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    public String lastName;

    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    public String email;

    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    public String password;
}
