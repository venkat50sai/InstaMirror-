package com.app.like.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface LikeService {
    void giveLike(int userId, int postId) throws Exception;
    int getCount(int id);
    boolean check(int userId, int postId);
    Page<Integer> getLikedUsers(int id, Pageable pageable);
    List<Integer> getLikedPosts(int id);
    void unLike(int userId, int postId) throws Exception;
    Map<Integer, Integer> getLikes(List<Integer> postId);
    void removeLikes(int postId);
    void deleteLikes(int id, int postId);
    void deleteLikes(int userId, List<Integer>postId);
}
