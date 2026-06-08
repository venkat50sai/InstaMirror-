package com.app.comment.restController;


import com.app.comment.dto.CommentDto;
import com.app.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentServ;

    @GetMapping("/give/{postId}")
    public ResponseEntity<Map<String,Object>> giveComment(@RequestHeader("X-User-Id") Integer userId, @PathVariable int postId, @RequestParam String comment){
        Map<String, Object> res = new HashMap<>();
        commentServ.giveComment(userId,postId,comment);
        res.put("success", true);
        res.put("message", "Commented Added");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/commentedUsers")
    public ResponseEntity<Page<CommentDto>> getCommentedUsers(@RequestParam("id")int id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size){
        Page<CommentDto> res = commentServ.getCommentedUsers(id, PageRequest.of(page, size));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<Integer,Integer>> getComments(@RequestBody List<Integer> postList){
        Map<Integer, Integer> res = new HashMap<>();
        res = commentServ.getComments(postList);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String,Object>> unLike(@RequestParam("id") int id)throws Exception{
        Map<String, Object> res = new HashMap<>();
        commentServ.deleteComment(id);
        res.put("message", "Comment Deleted");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/deleteAll/{id}")
    public ResponseEntity<Map<String,Object>> removeAllComments(@PathVariable int id)throws Exception{
        Map<String, Object> res = new HashMap<>();
        commentServ.removeAllComments(id);
        res.put("message", "Comment Deleted");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
