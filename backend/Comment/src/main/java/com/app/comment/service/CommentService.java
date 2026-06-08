package com.app.comment.service;


import com.app.comment.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CommentService {
    void giveComment(int userId, int postId, String comment);
    Page<CommentDto> getCommentedUsers(int id, Pageable pageable);
    List<Integer> getCommentedPosts(int id);
    int getCount(int id);
    void deleteComment(int id) throws Exception;
    Map<Integer, Integer> getComments(List<Integer> postId);
    void removeAllComments(int postId);
    void deleteComments(int userId, List<Integer> postId);
}
