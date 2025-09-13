package TechWiz.petOwner.repositories;

import TechWiz.petOwner.models.PetGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetGalleryRepository extends JpaRepository<PetGallery, Long> {
    
    // Find gallery images by pet ID and owner ID
    List<PetGallery> findByPetIdAndOwnerIdOrderByDisplayOrderAsc(Long petId, Long ownerId);
    
    // Find gallery images by pet ID
    List<PetGallery> findByPetIdOrderByDisplayOrderAsc(Long petId);
    
    // Count images by pet ID and owner ID
    @Query("SELECT COUNT(g) FROM PetGallery g WHERE g.petId = :petId AND g.ownerId = :ownerId")
    Long countByPetIdAndOwnerId(@Param("petId") Long petId, @Param("ownerId") Long ownerId);
    
    // Count images by pet ID
    @Query("SELECT COUNT(g) FROM PetGallery g WHERE g.petId = :petId")
    Long countByPetId(@Param("petId") Long petId);
    
    // Find gallery images by owner ID
    List<PetGallery> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    // Delete all images by pet ID and owner ID
    void deleteByPetIdAndOwnerId(Long petId, Long ownerId);
    
    // Delete all images by pet ID
    void deleteByPetId(Long petId);
    
    // Count images by owner ID
    @Query("SELECT COUNT(g) FROM PetGallery g WHERE g.ownerId = :ownerId")
    Long countByOwnerId(@Param("ownerId") Long ownerId);
}
