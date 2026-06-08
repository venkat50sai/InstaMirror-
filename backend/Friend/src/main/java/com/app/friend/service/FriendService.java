package com.app.friend.service;

import com.app.friend.entity.Friend;

import java.util.List;

public interface FriendService {
    void followUser(Integer followerId, Integer followingId) throws Exception;
    void unfollowUser(Integer followerId, Integer followingId) throws Exception;
    int getFollowersCount(Integer userId);
    int  getFollowingsCount(Integer userId);
    List<Integer> getFollowList(Integer userId);
    List<Integer> getFollowers(Integer userId);
    List<Integer> getFollowings(Integer userId);
    Friend checkFollow(Integer followerId, Integer followingId) throws Exception;
    void deleteFollow(Integer userId);
}
