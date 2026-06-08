package com.app.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    private int id;
    private int userId;
    private String caption;
    private byte[] image;
    private LocalDateTime createdAt;

    private String username;
    private byte[] userProfilePic;

    private boolean isLiked;
    private int likes;
    private int comments;
}