package com.app.comment.repository;

import com.app.comment.dto.CommentDto;
import com.app.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
    Integer countByPostId(int postId);
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId")
    Page<Comment> findUserIdsByPostId(@Param("postId") int postId, Pageable pageable);
    @Query("SELECT l.postId FROM Comment l WHERE l.userId = :userId")
    List<Integer> findPostIdsByUserId(@Param("userId") int userId);
    @Query("SELECT l.postId, COUNT(l) FROM Comment l WHERE l.postId IN :postIds GROUP BY l.postId")
    List<Object[]> countCommentsByPostIds(@Param("postIds") List<Integer> postIds);
    void deleteAllByPostId(int postId);
    @Query("DELETE FROM Comment c WHERE c.userId = :userId OR c.postId IN :postIds")
    void deleteComments(@Param("userId") int userId, @Param("postIds") List<Integer> postIds);
}
