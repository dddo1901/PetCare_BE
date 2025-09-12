package TechWiz.shelter.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.shelter.models.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    List<Pet> findByShelterId(Long shelterId);
    
    Page<Pet> findByShelterId(Long shelterId, Pageable pageable);
    
    List<Pet> findByAdoptionStatus(Pet.AdoptionStatus adoptionStatus);
    
    List<Pet> findByShelterIdAndAdoptionStatus(Long shelterId, Pet.AdoptionStatus adoptionStatus);
    
    @Query("SELECT p FROM Pet p WHERE p.shelter.id = :shelterId AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:breed IS NULL OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :breed, '%'))) AND " +
           "(:adoptionStatus IS NULL OR p.adoptionStatus = :adoptionStatus) AND " +
           "(:healthStatus IS NULL OR p.healthStatus = :healthStatus) AND " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:size IS NULL OR p.size = :size)")
    Page<Pet> findPetsWithFilters(@Param("shelterId") Long shelterId,
                                 @Param("name") String name,
                                 @Param("type") Pet.PetType type,
                                 @Param("breed") String breed,
                                 @Param("adoptionStatus") Pet.AdoptionStatus adoptionStatus,
                                 @Param("healthStatus") Pet.HealthStatus healthStatus,
                                 @Param("gender") Pet.Gender gender,
                                 @Param("size") Pet.Size size,
                                 Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.shelter.id = :shelterId AND p.adoptionStatus = :status")
    Long countByShelterIdAndAdoptionStatus(@Param("shelterId") Long shelterId, 
                                          @Param("status") Pet.AdoptionStatus status);
    
    @Query("SELECT p FROM Pet p WHERE p.adoptionStatus = 'AVAILABLE' AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:breed IS NULL OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :breed, '%'))) AND " +
           "(:gender IS NULL OR p.gender = :gender) AND " +
           "(:size IS NULL OR p.size = :size)")
    Page<Pet> findAvailablePetsWithFilters(@Param("type") Pet.PetType type,
                                          @Param("breed") String breed,
                                          @Param("gender") Pet.Gender gender,
                                          @Param("size") Pet.Size size,
                                          Pageable pageable);
}
