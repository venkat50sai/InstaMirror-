package com.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullname;
    private String phone;
    private LocalDate dob;
    private String bio;
}
