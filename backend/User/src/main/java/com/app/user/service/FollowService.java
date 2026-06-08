package com.app.user.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    ModelMapper modelMapper;

    @CircuitBreaker(name ="followServiceCB", fallbackMethod = "getFollowFromFollowServiceFallBack")
    public Map<String,Object> getFollowFromFollowService(Integer id){
        Map<String, Object> follow = webClientBuilder.build()
                .get()
                .uri("http://FOLLOW-SERVICE/friend/" + id + "/follow-stats")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
        return follow;
    }

    @CircuitBreaker(name = "followServiceCB", fallbackMethod = "checkFollowFallBack")
    public Boolean  checkIsFollowing(Integer followerId, Integer followingId) {

        Map<String, Object> response = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("FOLLOW-SERVICE")
                        .path("/friend/check")
                        .queryParam("id", followingId)
                        .build())
                .header("X-User-Id", followerId.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
        return response != null;
    }

//    @CircuitBreaker(name = "followServiceCB", fallbackMethod = "getFollowListFallBack")
    public List<Integer> getFollowList(Integer id, String status) {

        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("FOLLOW-SERVICE")
                        .path("/friend/followList")
                        .queryParam("id", id)
                        .queryParam("status", status)
                        .build())
                .header("X-User-Id", id.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Integer>>() {})
                .block();
    }

    public Map<String,Object> getFollowFromFollowServiceFallBack(Integer id, Throwable ex){
        return new HashMap<>();
    }

    public Boolean checkFollowFallBack(Integer followerId,Integer followingId, Throwable ex) {
        return null;
    }

    public List<Integer> getFollowListFallBack(Integer id, String status, Throwable ex){return new ArrayList<>();}

}
