package TechWiz.shelter.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.shelter.models.ShelterPet;

@Repository
public interface ShelterPetRepository extends JpaRepository<ShelterPet, Long> {
    
    List<ShelterPet> findByShelterProfileId(Long shelterProfileId);
    
    List<ShelterPet> findByShelterProfileIdOrderByCreatedAtDesc(Long shelterProfileId);
    
    Page<ShelterPet> findByShelterProfileId(Long shelterProfileId, Pageable pageable);
    
    List<ShelterPet> findByAdoptionStatus(ShelterPet.AdoptionStatus adoptionStatus);
    
    List<ShelterPet> findByShelterProfileIdAndAdoptionStatus(Long shelterProfileId, ShelterPet.AdoptionStatus adoptionStatus);
    
    Long countByShelterProfileId(Long shelterProfileId);
    
    @Query("SELECT p FROM ShelterPet p WHERE p.shelterProfile.id = :shelterProfileId AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:breed IS NULL OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :breed, '%'))) AND " +
           "(:adoptionStatus IS NULL OR p.adoptionStatus = :adoptionStatus) AND " +
           "(:healthStatus IS NULL OR p.healthStatus = :healthStatus) AND " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:size IS NULL OR p.size = :size)")
    Page<ShelterPet> findPetsWithFilters(@Param("shelterProfileId") Long shelterProfileId,
                                 @Param("name") String name,
                                 @Param("type") ShelterPet.PetType type,
                                 @Param("breed") String breed,
                                 @Param("adoptionStatus") ShelterPet.AdoptionStatus adoptionStatus,
                                 @Param("healthStatus") ShelterPet.HealthStatus healthStatus,
                                 @Param("gender") ShelterPet.Gender gender,
                                 @Param("size") ShelterPet.Size size,
                                 Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM ShelterPet p WHERE p.shelterProfile.id = :shelterProfileId AND p.adoptionStatus = :status")
    Long countByShelterProfileIdAndAdoptionStatus(@Param("shelterProfileId") Long shelterProfileId, 
                                                @Param("status") ShelterPet.AdoptionStatus status);
    
    @Query("SELECT p FROM ShelterPet p WHERE p.adoptionStatus = 'AVAILABLE' AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:breed IS NULL OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :breed, '%'))) AND " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:size IS NULL OR p.size = :size)")
    Page<ShelterPet> findAvailablePetsWithFilters(@Param("type") ShelterPet.PetType type,
                                          @Param("breed") String breed,
                                          @Param("gender") ShelterPet.Gender gender,
                                          @Param("size") ShelterPet.Size size,
                                          Pageable pageable);
}
