package com.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFullDetailsDto {
    private UserDto user;
    private List<UserDto> followers;
    private List<UserDto> followings;
}
