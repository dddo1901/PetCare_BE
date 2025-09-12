package TechWiz.admin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import TechWiz.admin.models.PostCategory;
import TechWiz.admin.models.dto.CreatePostRequest;
import TechWiz.admin.services.PostService;
import TechWiz.auths.models.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/posts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostManagementController {
    
    @Autowired
    private PostService postService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            HttpServletRequest httpRequest) {
        
        Long authorId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = postService.createPost(request, authorId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody CreatePostRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = postService.updatePost(postId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePost(
            @PathVariable Long postId,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = postService.deletePost(postId, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = postService.getAllPosts(page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/published")
    public ResponseEntity<ApiResponse> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = postService.getPublishedPosts(page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getPostsByCategory(
            @PathVariable PostCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = postService.getPostsByCategory(category, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse> getPostById(@PathVariable Long postId) {
        ApiResponse response = postService.getPostById(postId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = postService.searchPosts(keyword, page, size);
        return ResponseEntity.ok(response);
    }
}
