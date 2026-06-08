package com.app.friend.serviceImpl;

import com.app.friend.dto.FriendDto;
import com.app.friend.entity.Friend;
import com.app.friend.repository.FriendRepository;
import com.app.friend.service.FriendService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    @Autowired
    FriendRepository friendRepo;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public void followUser(Integer followerId, Integer followingId) throws Exception{
        if (followerId.equals(followingId)) {
            throw new Exception("Users cannot follow themselves.");
        }
        if (friendRepo.findByFollowerIdAndFollowingId(followerId, followingId).isPresent()) {
            throw new Exception("You are already following this user.");
        }

        Friend friend = new Friend();
        friend.setFollowerId(followerId);
        friend.setFollowingId(followingId);
        friendRepo.save(friend);
    }



    @Override
    public void unfollowUser(Integer followerId, Integer followingId) throws Exception{
        friendRepo
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() ->
                        new Exception("You are not following this user."));
        friendRepo.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public int getFollowersCount(Integer userId) {
        return friendRepo.countByFollowerId(userId);
    }

    @Override
    public int getFollowingsCount(Integer userId) {
        return friendRepo.countByFollowingId(userId);
    }

    @Override
    public List<Integer> getFollowers(Integer userId) {
        List<Friend> followers = friendRepo.findByFollowingId(userId);
        return followers.stream()
                .map(Friend::getFollowerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getFollowings(Integer userId) {
        List<Friend> followings = friendRepo.findByFollowerId(userId);
        return followings.stream()
                .map(Friend::getFollowingId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getFollowList(Integer userId) {
        List<Friend> followings = friendRepo.findByFollowingId(userId);
        return followings.stream()
                .map(Friend::getFollowerId)
                .collect(Collectors.toList());
    }

    @Override
    public Friend checkFollow(Integer followerId, Integer followingId) throws Exception{
        Friend friend = friendRepo
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new Exception("Follow request not found"));
        System.out.println(followerId+"  "+ followingId+ "  "+ friend);
        return friend;
    }

    @Override
    public void deleteFollow(Integer userId) {
        friendRepo.deleteByFollowerOrFollowing(userId);
    }

}
