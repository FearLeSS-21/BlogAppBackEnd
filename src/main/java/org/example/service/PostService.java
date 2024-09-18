package org.example.service;

import org.example.model.Post;
import org.example.model.User;
import org.example.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post, User user) {
        post.setUser(user);
        return postRepository.save(post);
    }

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUserId(user.getId());
    }

    public Post updatePost(Long postId, Post postDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postDetails.getTitle());
        post.setContent(postDetails.getContent());

        return postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
