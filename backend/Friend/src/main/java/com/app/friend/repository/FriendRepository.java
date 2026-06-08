package com.app.friend.repository;

import com.app.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Integer> {

    Optional<Friend> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
    List<Friend> findByFollowingId(Integer followingId);
    List<Friend> findByFollowerId(Integer followingId);
    int countByFollowerId(int id);
    int countByFollowingId(int id);

    void deleteByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);

    @Query("DELETE FROM Friend f WHERE f.followerId = :userId OR f.followingId = :userId")
    int deleteByFollowerOrFollowing(@Param("userId") Integer userId);
}
