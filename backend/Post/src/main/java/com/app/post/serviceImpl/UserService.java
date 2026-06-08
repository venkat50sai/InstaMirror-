package com.app.post.serviceImpl;

import com.app.post.dto.UserInfoDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getUsersFromUserServiceFallback")
    public Map<Integer, UserInfoDto> getUsersFromUserService(Set<Integer> userIds) {
        List<UserInfoDto> users =  webClientBuilder.build()
                .post()
                .uri("http://USER-SERVICE/user/batch")
                .bodyValue(userIds)
                .retrieve()
                .bodyToFlux(UserInfoDto.class)
                .collectList()
                .block();

        return users.stream().collect(Collectors.toMap(UserInfoDto::getId, user -> user));
    }

    public Map<Integer, UserInfoDto> getUsersFromUserServiceFallback(Set<Integer> userIds, Throwable ex) {
        Map<Integer, UserInfoDto> fallbackMap = new HashMap<>();
        for (Integer id : userIds) {
            UserInfoDto user = new UserInfoDto();
            user.setId(id);
            user.setUsername("Unknown User");
            user.setFullname("N/A");
            user.setImage(null);
            fallbackMap.put(id, user);
        }
        return fallbackMap;
    }

}
