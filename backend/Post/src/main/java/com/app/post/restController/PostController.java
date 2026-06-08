package com.app.post.restController;

import com.app.post.dto.PostDto;
import com.app.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postServ;

    @PostMapping("/create")
    public ResponseEntity<Map<String,Object>> createPost(@RequestParam("userId") int userId, @RequestParam("caption") String caption, @RequestParam("image") MultipartFile imageFile) throws Exception{
        Map<String, Object> res = new HashMap<>();
        postServ.createPost(userId, caption, imageFile);
        res.put("success", true);
        res.put("message", "Post created successfully.");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/getAllPosts")
    public ResponseEntity<List<PostDto>> getAllPosts(@RequestHeader("X-User-Id") Integer userId) {
        List<PostDto> posts = postServ.getAllPosts(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/getUserPost")
    public ResponseEntity<?> getPostsByUserId(@RequestParam("id") int id) throws Exception{
        List<PostDto> posts = postServ.getPostByUserId(id);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String,Object>> deletePost(@RequestParam("id") int Id) throws Exception{
        Map<String, Object> res = new HashMap<>();
        postServ.deletePost(Id);
        res.put("success", true);
        res.put("message", "Post deleted successfully.");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/post-Count")
    public ResponseEntity<Map<String, Object>> Count() throws Exception{
        Map<String , Object> m = new HashMap<>();
        m.put("count", postServ.getCount());
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    @GetMapping("/count/{id}")
    public ResponseEntity<Map<String,Object>> count(@PathVariable int id) throws Exception{
        Map<String, Object> res = new HashMap<>();
        int count = postServ.count(id);
        res.put("success", true);
        res.put("count",count);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
