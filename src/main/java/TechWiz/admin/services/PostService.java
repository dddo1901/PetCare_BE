package TechWiz.admin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.admin.models.Post;
import TechWiz.admin.models.PostCategory;
import TechWiz.admin.models.dto.CreatePostRequest;
import TechWiz.admin.repositories.PostRepository;
import TechWiz.auths.models.User;
import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.repositories.UserRepository;

@Service
@Transactional
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public ApiResponse createPost(CreatePostRequest request, Long authorId) {
        try {
            Optional<User> userOptional = userRepository.findById(authorId);
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("Author not found!");
            }
            
            User author = userOptional.get();
            
            Post post = new Post();
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setCategory(request.getCategory());
            post.setImageUrl(request.getImageUrl());
            post.setAuthorId(authorId);
            post.setAuthorName(author.getFullName());
            post.setIsPublished(request.getIsPublished());
            post.setCreatedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());
            
            Post savedPost = postRepository.save(post);
            
            return ApiResponse.success("Post created successfully!", savedPost);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to create post: " + e.getMessage());
        }
    }
    
    public ApiResponse updatePost(Long postId, CreatePostRequest request, Long authorId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            
            if (postOptional.isEmpty()) {
                return ApiResponse.error("Post not found!");
            }
            
            Post post = postOptional.get();
            
            // Check if user is the author or admin
            Optional<User> userOptional = userRepository.findById(authorId);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }
            
            User user = userOptional.get();
            if (!post.getAuthorId().equals(authorId) && !user.getRole().name().equals("ADMIN")) {
                return ApiResponse.error("You can only edit your own posts!");
            }
            
            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setCategory(request.getCategory());
            post.setImageUrl(request.getImageUrl());
            post.setIsPublished(request.getIsPublished());
            post.setUpdatedAt(LocalDateTime.now());
            
            Post updatedPost = postRepository.save(post);
            
            return ApiResponse.success("Post updated successfully!", updatedPost);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to update post: " + e.getMessage());
        }
    }
    
    public ApiResponse deletePost(Long postId, Long userId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            
            if (postOptional.isEmpty()) {
                return ApiResponse.error("Post not found!");
            }
            
            Post post = postOptional.get();
            
            // Check if user is the author or admin
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }
            
            User user = userOptional.get();
            if (!post.getAuthorId().equals(userId) && !user.getRole().name().equals("ADMIN")) {
                return ApiResponse.error("You can only delete your own posts!");
            }
            
            postRepository.deleteById(postId);
            
            return ApiResponse.success("Post deleted successfully!");
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete post: " + e.getMessage());
        }
    }
    
    public ApiResponse getAllPosts(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
            
            return ApiResponse.success("Posts retrieved successfully", posts.getContent());
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve posts: " + e.getMessage());
        }
    }
    
    public ApiResponse getPublishedPosts(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postRepository.findByIsPublishedTrueOrderByCreatedAtDesc(pageable);
            
            return ApiResponse.success("Published posts retrieved successfully", posts.getContent());
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve published posts: " + e.getMessage());
        }
    }
    
    public ApiResponse getPostsByCategory(PostCategory category, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postRepository.findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(category, pageable);
            
            return ApiResponse.success("Posts by category retrieved successfully", posts.getContent());
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve posts by category: " + e.getMessage());
        }
    }
    
    public ApiResponse getPostById(Long postId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            
            if (postOptional.isEmpty()) {
                return ApiResponse.error("Post not found!");
            }
            
            return ApiResponse.success("Post retrieved successfully", postOptional.get());
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve post: " + e.getMessage());
        }
    }
    
    public ApiResponse searchPosts(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postRepository.searchPublishedPosts(keyword, pageable);
            
            return ApiResponse.success("Search completed successfully", posts.getContent());
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to search posts: " + e.getMessage());
        }
    }
}
