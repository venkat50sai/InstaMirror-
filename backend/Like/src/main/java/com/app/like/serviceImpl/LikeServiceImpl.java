package com.app.like.serviceImpl;

import com.app.like.entity.Like;
import com.app.like.exception.LikeException;
import com.app.like.repository.LikeRepository;
import com.app.like.service.LikeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

    @Autowired
    LikeRepository likeRepo;

    @Autowired
    ModelMapper modelMapper;
    
    @Override
    public void giveLike(int userId, int postId) throws LikeException {
        boolean isExists = check(userId, postId);
        if(isExists)
            throw new LikeException("Already liked !!!");
        Like like = new Like();
        like.setUserId(userId);
        like.setPostId(postId);
        likeRepo.save(like);
    }

    @Override
    public boolean check(int userId, int postId){
        return likeRepo.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public Page<Integer> getLikedUsers(int id, Pageable pageable) {
        return likeRepo.findUserIdsByPostId(id, pageable);
    }

    @Override
    public List<Integer> getLikedPosts(int id) {
        return likeRepo.findPostIdsByUserId(id);
    }

    @Override
    public int getCount(int id){
        return likeRepo.countByPostId(id);
    }

    @Override
    public void unLike(int userId, int postId) throws LikeException{
        boolean isExists = check(userId, postId);
        if(!isExists)
            throw new LikeException("User have not liked this post");
        likeRepo.deleteByUserIdAndPostId(userId, postId);
    }

    @Override
    public void removeLikes(int postId){
        likeRepo.deleteAllByPostId(postId);
    }

    @Override
    public void deleteLikes(int userId, int postId) {
        likeRepo.deleteByUserIdAndPostId(userId, postId);
    }

    @Override
    public void deleteLikes(int userId, List<Integer> postId) {
        likeRepo.deleteLikes(userId, postId);
    }

    @Override
    public Map<Integer, Integer> getLikes(List<Integer> postId) {
        Map<Integer, Integer> res = new HashMap<>();
        List<Object[]> results = likeRepo.countLikesByPostIds(postId);
        for (Object[] result : results) {
            int Id = (Integer) result[0];
            Long count = (Long) result[1];
            res.put(Id, count.intValue());
        }
        return res;
    }
}
