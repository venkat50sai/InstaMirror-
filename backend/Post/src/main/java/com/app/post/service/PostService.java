package com.app.post.service;

import com.app.post.dto.PostDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    void createPost(int userId, String caption, MultipartFile imageFile) throws Exception;
    List<PostDto> getAllPosts (int id);
    List<PostDto> getPostByUserId(int userId) throws Exception;
    void deletePost(int postId) throws Exception;
    int count(int id);
    int getCount();
}
