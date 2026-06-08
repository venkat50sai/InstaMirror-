package com.app.post.repository;

import com.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {

    List<Post> findByUserId(Integer userId);
    int countByUserId(int userId);
    @Query("Select COUNT(p) from Post p")
    long getCount();
}
