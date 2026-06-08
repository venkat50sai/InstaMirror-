package com.app.like.restController;

import com.app.like.dto.LikeDto;
import com.app.like.entity.Like;
import com.app.like.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    LikeService likeService;

    @GetMapping("/give")
    public ResponseEntity<Map<String, Object>> giveLike(@RequestHeader("X-User-Id") Integer userId, @RequestParam("id") int postId) throws Exception {
        Map<String, Object> res = new HashMap<>();
        likeService.giveLike(userId, postId);
        res.put("success", true);
        res.put("message", "Liked Successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<Map<String, Object>> getCount(@PathVariable int id) {
        Map<String, Object> res = new HashMap<>();
        int count = likeService.getCount(id);
        res.put("count", count);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/likedUsers")
    public ResponseEntity<Page<Integer>> getLikedUsers(@RequestParam("id") int id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        Page<Integer> res = likeService.getLikedUsers(id, PageRequest.of(page, size));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/likedPosts/{id}")
    public ResponseEntity<List<Integer>> getLikedPosts(@PathVariable int id) {
        List<Integer> res = likeService.getLikedPosts(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/verify/{postId}")
    public ResponseEntity<Map<String, Object>> verifyLike(@RequestHeader("X-User-Id") Integer userId, @PathVariable int postId) {
        Map<String, Object> res = new HashMap<>();
        boolean verify = likeService.check(userId, postId);
        res.put("liked", verify);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<Integer, Integer>> getLikes(@RequestBody List<Integer> postList) {
        Map<Integer, Integer> res = new HashMap<>();
        res = likeService.getLikes(postList);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/unlike")
    public ResponseEntity<Map<String, Object>> unLike(@RequestHeader("X-User-Id") Integer userId, @RequestParam("id") int postId) throws Exception {
        Map<String, Object> res = new HashMap<>();
        likeService.unLike(userId, postId);
        res.put("message", "Unliked Successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Map<String, Object>> removeLikes(@PathVariable int postId){
        Map<String, Object> res = new HashMap<>();
        likeService.removeLikes(postId);
        res.put("message", "Unliked Successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/removeLike")
    public ResponseEntity<Map<String, Object>> remove(@RequestParam("id") int id, @RequestParam("postId") int postId){
        Map<String, Object> res = new HashMap<>();
        likeService.deleteLikes(id, postId);
        res.put("message", "Deleted Successfully");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
