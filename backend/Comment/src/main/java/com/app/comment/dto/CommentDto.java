package com.app.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private int commentId;
    private int id;
    private String username;
    private String fullname;
    private byte[] image;
    private String comment;
}