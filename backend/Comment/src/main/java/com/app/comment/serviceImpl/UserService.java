package com.app.comment.serviceImpl;

import com.app.comment.dto.CommentDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @CircuitBreaker(name="userServiceCB", fallbackMethod = "getUsersFromUserServiceFallBack")
    public List<CommentDto> getUsersFromUserService(Set<Integer> userIds){
        List<CommentDto> commentDtos = webClientBuilder.build()
                .post()
                .uri("http://USER-SERVICE/user/batch")
                .bodyValue(userIds)
                .retrieve()
                .bodyToFlux(CommentDto.class)
                .collectList()
                .block();
        return commentDtos;
    }

    public List<CommentDto> getUsersFromUserServiceFallBack(Set<Integer> userIds, Throwable ex){
        List<CommentDto> commentDtos = new ArrayList<>();
        for(Integer id: userIds){
            CommentDto comment = new CommentDto();
            comment.setId(id);
            comment.setFullname("FULL-NAME");
            comment.setUsername("USER-NAME");
            commentDtos.add(comment);
        }
        return commentDtos;
    }
}
