package TechWiz.admin.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.admin.models.Post;
import TechWiz.admin.models.PostCategory;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // Find all published posts
    Page<Post> findByIsPublishedTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Find posts by category
    Page<Post> findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(PostCategory category, Pageable pageable);
    
    // Find posts by author
    Page<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
    
    // Search posts by title or content
    @Query("SELECT p FROM Post p WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.isPublished = true " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchPublishedPosts(@Param("keyword") String keyword, Pageable pageable);
    
    // Find all posts for admin (including unpublished)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Count posts by category
    Long countByCategoryAndIsPublishedTrue(PostCategory category);
}
