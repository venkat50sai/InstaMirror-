package com.app.comment.serviceImpl;

import com.app.comment.dto.CommentDto;
import com.app.comment.entity.Comment;
import com.app.comment.exception.CommentException;
import com.app.comment.repository.CommentRepository;
import com.app.comment.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private UserService userService;

    @Override
    public void giveComment(int userId, int postId, String comment){
        Comment com = new Comment();
        com.setComment(comment);
        com.setPostId(postId);
        com.setUserId(userId);
        commentRepo.save(com);
    }

    @Override
    public Page<CommentDto> getCommentedUsers(int id, Pageable pageable) {
        Page<Comment> comments = commentRepo.findUserIdsByPostId(id, pageable);
        Set<Integer> userIds = comments.stream().map(Comment::getUserId).collect(Collectors.toSet());
        List<CommentDto> commentDtos = userService.getUsersFromUserService(userIds);

        Map<Integer, CommentDto> userInfoMap = commentDtos.stream()
                .filter(dto -> dto.getId()!=0)
                .collect(Collectors.toMap(
                        CommentDto::getId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<CommentDto> finalDtos = comments.stream()
                .map(comment -> {
                    CommentDto userDto = userInfoMap.get(comment.getUserId());
                        return new CommentDto(
                                comment.getId(),
                                comment.getUserId(),
                                userDto.getUsername(),
                                userDto.getFullname(),
                                userDto.getImage(),
                                comment.getComment()
                        );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(finalDtos, comments.getPageable(), comments.getTotalElements());
    }

    @Override
    public List<Integer> getCommentedPosts(int id) {
        return commentRepo.findPostIdsByUserId(id);
    }

    @Override
    public int getCount(int id){
        return commentRepo.countByPostId(id);
    }

    @Override
    public void deleteComment(int id) throws CommentException {
        Optional<Comment> optional = commentRepo.findById(id);
        if(optional.isEmpty())
            throw new CommentException("You have not commented on this post");
        commentRepo.deleteById(id);
    }

    @Override
    public Map<Integer, Integer> getComments(List<Integer> postId) {
        Map<Integer, Integer> res = new HashMap<>();
        List<Object[]> results = commentRepo.countCommentsByPostIds(postId);
        for (Object[] result : results) {
            int Id = (Integer) result[0];
            Long count = (Long) result[1];
            res.put(Id, count.intValue());
        }
        return res;
    }

    @Override
    public void removeAllComments(int postId) {
        commentRepo.deleteAllByPostId(postId);
    }

    @Override
    public void deleteComments(int userId, List<Integer> postIds) {
        commentRepo.deleteComments(userId, postIds);
    }

}
