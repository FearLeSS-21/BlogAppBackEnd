package org.example.controller;

import org.example.model.Post;
import org.example.model.User;
import org.example.service.PostService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post) {
        User user = userService.findById(post.getUser().getId());
        post.setUser(user);

        Post createdPost = postService.createPost(post);
        return ResponseEntity.ok().body("Post created successfully: " + createdPost);
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable Long postId) {
        Optional<Post> post = postService.getPostById(postId);
        if (post.isPresent()) {
            return ResponseEntity.ok(post.get());
        } else {
            return ResponseEntity.status(404).body("Post not found");
        }
    }


    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody Post updatedPost) {
        Post post = postService.getPostById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(404).body("Post not found");
        }

        User user = userService.findById(updatedPost.getUser().getId());

        // Ensure the user is the author of the post
        if (!post.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Forbidden: You are not the author of this post");
        }

        updatedPost.setUser(user);
        Post updated = postService.updatePost(postId, updatedPost);
        return ResponseEntity.ok().body("Post updated successfully: " + updated);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @RequestParam Long userId) {
        Optional<Post> postOptional = postService.getPostById(postId);

        if (!postOptional.isPresent()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        Post post = postOptional.get();

        // Check if the logged-in user's ID matches the post's author's ID
        if (!post.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("Forbidden: You are not the author of this post");
        }

        postService.deletePost(postId);
        return ResponseEntity.ok().body("Post deleted successfully");
    }

}