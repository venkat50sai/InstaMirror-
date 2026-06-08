package com.app.post.serviceImpl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LikeService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @CircuitBreaker(name="likeServiceCB", fallbackMethod = "getLikesCountfromLikesServiceFallBack")
    public Map<Integer, Integer> getLikesCountfromLikesService(List<Integer> postIds){
        return webClientBuilder.build()
                .post()
                .uri("http://LIKE-SERVICE/like/batch")
                .bodyValue(postIds)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Integer, Integer>>() {})
                .block();
    }

    @CircuitBreaker(name="likeServiceCB", fallbackMethod = "getLikesfromLikesServiceFallBack")
    public Set<Integer> getLikesfromLikesService(Integer currentUser){
        return webClientBuilder.build()
                .get()
                .uri("http://LIKE-SERVICE/like/likedPosts/" + currentUser)
                .retrieve()
                .bodyToFlux(Integer.class)
                .collect(Collectors.toSet())
                .block();
    }

    public Map<Integer, Integer> getLikesCountfromLikesServiceFallBack(List<Integer> postIds, Throwable ex) {
        Map<Integer, Integer> likesCount = new HashMap<>();
        for (Integer id : postIds) {
            likesCount.put(id, 0);
        }
        return likesCount;
    }

    public Set<Integer> getLikesfromLikesServiceFallBack(Integer currentUser, Throwable ex) {
        return Collections.emptySet();
    }
}
