package com.app.like.repository;

import com.app.like.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Integer> {
    Integer countByPostId(int postId);
    boolean existsByUserIdAndPostId(Integer userId, Integer postId);
    void deleteByUserIdAndPostId(int userId, int postId);
    @Query("SELECT l.userId FROM Like l WHERE l.postId = :postId")
    Page<Integer> findUserIdsByPostId(@Param("postId") int postId, Pageable pageable);
    @Query("SELECT l.postId FROM Like l WHERE l.userId = :userId")
    List<Integer> findPostIdsByUserId(@Param("userId") int userId);
    @Query("SELECT l.postId, COUNT(l) FROM Like l WHERE l.postId IN :postIds GROUP BY l.postId")
    List<Object[]> countLikesByPostIds(@Param("postIds") List<Integer> postIds);
    void deleteAllByPostId(int postId);
    @Query("DELETE FROM Like l WHERE l.userId = :userId OR l.postId IN :postIds")
    void deleteLikes(@Param("userId") int userId, @Param("postIds") List<Integer> postIds);
}
