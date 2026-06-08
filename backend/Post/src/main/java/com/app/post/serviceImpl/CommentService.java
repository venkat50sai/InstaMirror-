package com.app.post.serviceImpl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "commentServiceCB", fallbackMethod = "getCountFromCommentServiceFallBack")
    public Map<Integer, Integer> getCountFromCommentService(List<Integer>postIds){
        return webClientBuilder.build()
                .post()
                .uri("http://COMMENT-SERVICE/comment/batch")
                .bodyValue(postIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Integer, Integer>>() {})
                .block();
    }

    public Map<Integer, Integer> getCountFromCommentServiceFallBack(List<Integer> postIds, Throwable ex){
        Map<Integer,Integer> commentCount = new HashMap<>();
        for(Integer id: postIds){
            commentCount.put(id, 0);
        }
        return commentCount;
    }
}
