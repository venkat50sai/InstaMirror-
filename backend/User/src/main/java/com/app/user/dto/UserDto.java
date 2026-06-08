package com.app.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private int id;
    private String username;
    private String email;
    private String fullname;
    private String phone;
    private byte[] image;
    private LocalDate dob;
    private String bio;
    private int followers;
    private int followings;
    private int posts;
    private Boolean self;
    private Boolean followStatus;
}