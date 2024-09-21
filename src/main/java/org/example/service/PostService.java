package org.example.service;

import org.example.model.Post;
import org.example.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // Create a new post
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // Retrieve all posts with user details
    public List<Post> getAllPosts() {
        return postRepository.findAll(); // Fetch all posts
    }

    // Retrieve a post by ID
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    // Update an existing post
    public Post updatePost(Long postId, Post updatedPost) {
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        return postRepository.save(existingPost);
    }

    // Delete a post
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
