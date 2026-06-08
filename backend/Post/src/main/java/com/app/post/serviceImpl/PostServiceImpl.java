package com.app.post.serviceImpl;

import com.app.post.dto.PostDto;
import com.app.post.dto.UserInfoDto;
import com.app.post.entity.Post;
import com.app.post.exception.PostException;
import com.app.post.repository.PostRepository;
import com.app.post.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Override
    public void createPost(int userId, String caption, MultipartFile imageFile) throws Exception {
        Post post = new Post();
        post.setUserId(userId);
        post.setCaption(caption);
        post.setImage(imageFile.getBytes());
        postRepo.save(post);
    }

    @Override
    public List<PostDto> getAllPosts(int currentUser) {
        List<Post> posts = postRepo.findAll();
        List<PostDto> postDto = posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());
        enrichPosts(postDto, currentUser);
        return postDto;
    }

    private void enrichPosts(List<PostDto> postDto, int currentUser) {
        Set<Integer> userIds = postDto.stream()
                .map(PostDto::getUserId)
                .collect(Collectors.toSet());
        List<Integer> postIds = postDto.stream()
                .map(PostDto::getId)
                .collect(Collectors.toList());

        Map<Integer, UserInfoDto> userMap = userService.getUsersFromUserService(userIds);
        Map<Integer, Integer> likeCount = likeService.getLikesCountfromLikesService(postIds);
        Set<Integer> likedPosts = likeService.getLikesfromLikesService(currentUser);
        Map<Integer, Integer> commentCount = commentService.getCountFromCommentService(postIds);
        for (PostDto post : postDto) {
            UserInfoDto user = userMap.get(post.getUserId());
            if (user != null) {
                post.setUsername(user.getUsername());
                post.setUserProfilePic(user.getImage());
            }
            post.setLikes(likeCount.getOrDefault(post.getId(), 0));
            post.setComments(commentCount.getOrDefault(post.getId(), 0));
            post.setLiked(likedPosts.contains(post.getId()));
        }
    }

    @Override
    public List<PostDto> getPostByUserId(int userId) throws PostException {
        List<Post> postList = postRepo.findByUserId(userId);
        if(postList.isEmpty()) throw new PostException("No posts are there");
        List<Integer> postIds = postList.stream()
                .map(Post::getId)
                .collect(Collectors.toList());
        Map<Integer, Integer> likeCount = likeService.getLikesCountfromLikesService(postIds);
        Map<Integer, Integer> commentCount =  commentService.getCountFromCommentService(postIds);
        List<PostDto> postDto = new ArrayList<>();
        for (Post post : postList) {
            PostDto dto = modelMapper.map(post, PostDto.class);
            dto.setLikes(likeCount.getOrDefault(dto.getId(), 0));
            dto.setComments(commentCount.getOrDefault(dto.getId(),0));
            postDto.add(dto);
        }
        return postDto;
    }

    @Override
    public void deletePost(int postId) throws PostException {
        Post post = postRepo.findById(postId).orElseThrow(()->new PostException("No post found for this id:" + postId));
        webClientBuilder.build()
                .delete()
                .uri("http://LIKE-SERVICE/like/delete/"+postId)
                .retrieve()
                .bodyToMono(void.class)
                .block();
        webClientBuilder.build()
                .delete()
                .uri("http://COMMENT-SERVICE/comment/deleteAll/"+postId)
                .retrieve()
                .bodyToMono(void.class)
                .block();
        postRepo.deleteById(postId);
    }

    @Override
    public int count(int id) {
        return postRepo.countByUserId(id);
    }

    @Override
    public int getCount() {
        return (int)postRepo.getCount();
    }
}
