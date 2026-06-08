package com.app.like.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private int id;
    private int userId;
    private int postId;
    private LocalDateTime createdAt;
}