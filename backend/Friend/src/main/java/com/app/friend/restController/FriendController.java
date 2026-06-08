package com.app.friend.restController;

import com.app.friend.entity.Friend;
import com.app.friend.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    FriendService friendService;

    @PostMapping("/follow")
    public ResponseEntity<Map<String,Object>> followUser(@RequestHeader("X-User-Id") Integer followerId, @RequestParam("id") Integer followingId) throws Exception{
        Map<String,Object> response = new HashMap<>();
        friendService.followUser(followerId, followingId);
        response.put("success", true);
        response.put("message", "Sent Request");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<Map<String,Object>> unfollowUser(@RequestHeader("X-User-Id") Integer  followerId, @RequestParam("id") Integer followingId) throws Exception {
        Map<String,Object> response = new HashMap<>();
        friendService.unfollowUser(followerId, followingId);
        response.put("success", true);
        response.put("message", "Unfollowed Successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/followers")
    public ResponseEntity<List<Integer>> getFollowers(@RequestParam("id") int id) {
        List<Integer> followers = friendService.getFollowers(id);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/followings")
    public ResponseEntity<List<Integer>> getFollowings(@RequestParam("id") int id) {
        List<Integer> followings = friendService.getFollowings(id);
        return ResponseEntity.ok(followings);
    }

    @GetMapping("/followList")
    public ResponseEntity<List<Integer>> getFollowList(@RequestParam("id") int id) {
        List<Integer> followings = friendService.getFollowList(id);
        return ResponseEntity.ok(followings);
    }

    @GetMapping("/{id}/follow-stats")
    public ResponseEntity<Map<String, Integer>> getFollowStats(@PathVariable int id) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("followersCount", friendService.getFollowersCount(id));
        stats.put("followingsCount", friendService.getFollowingsCount(id));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String,Object>> Check(@RequestHeader("X-User-Id") Integer  followerId, @RequestParam("id") Integer followingId) throws Exception{
        Map<String,Object> response = new HashMap<>();
        response.put("success", true);
        Friend friend = friendService.checkFollow(followerId, followingId);
        response.put("stats", friend);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
