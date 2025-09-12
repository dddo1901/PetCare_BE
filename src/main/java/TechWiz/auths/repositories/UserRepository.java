package TechWiz.auths.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import TechWiz.auths.models.User;
import TechWiz.auths.models.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByIsEmailVerifiedFalse();
    
    // New methods for admin management
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<User> findByRoleOrderByCreatedAtDesc(Role role, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY u.createdAt DESC")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
