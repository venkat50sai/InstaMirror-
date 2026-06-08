package com.app.user.service;

import com.app.user.dto.PostDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @CircuitBreaker(name ="postServiceCB", fallbackMethod = "getCountfromPostServiceFallBack")
    public Map<String,Object> getCountfromPostService(Integer id){
        Map<String, Object> post = webClientBuilder.build()
                .get()
                .uri("http://POST-SERVICE/post/count/" + id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
        return post;
    }

    @CircuitBreaker(name="postServiceCB", fallbackMethod = "getPostsFallBack")
    public List<PostDto> getPosts(Integer id){
        return webClientBuilder.build()
                .get()
                .uri("http://POST-SERVICE/post/getUserPost?id="+id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PostDto>>() {})
                .block();
    }

    public Map<String,Object> getCountfromPostServiceFallBack(Integer id, Throwable ex){
        return new HashMap<>();
    }

    public List<PostDto> getPostsFallBack(Integer id, Throwable ex){ return new ArrayList<>(); }
}
