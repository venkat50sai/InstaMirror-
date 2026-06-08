package com.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Integer id;
    private String caption;
    private byte[] image;
    private Integer likes;
    private Integer comments;
    private boolean liked;
}
