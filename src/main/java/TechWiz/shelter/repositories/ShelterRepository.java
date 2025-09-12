package TechWiz.shelter.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.shelter.models.Shelter;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {
    
    Optional<Shelter> findByEmail(String email);
    
    Optional<Shelter> findByEmailAndIsVerifiedTrue(String email);
    
    List<Shelter> findByStatus(Shelter.ShelterStatus status);
    
    List<Shelter> findByIsVerifiedTrue();
    
    @Query("SELECT s FROM Shelter s WHERE " +
           "(:shelterName IS NULL OR LOWER(s.shelterName) LIKE LOWER(CONCAT('%', :shelterName, '%'))) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:isVerified IS NULL OR s.isVerified = :isVerified)")
    Page<Shelter> findSheltersWithFilters(@Param("shelterName") String shelterName,
                                         @Param("status") Shelter.ShelterStatus status,
                                         @Param("isVerified") Boolean isVerified,
                                         Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM Shelter s WHERE s.status = :status")
    Long countByStatus(@Param("status") Shelter.ShelterStatus status);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
}
