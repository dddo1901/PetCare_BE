package TechWiz.shelter.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.shelter.models.AdoptionInquiry;

@Repository
public interface AdoptionInquiryRepository extends JpaRepository<AdoptionInquiry, Long> {
    
    List<AdoptionInquiry> findByShelterProfileId(Long shelterProfileId);
    
    Page<AdoptionInquiry> findByShelterProfileId(Long shelterProfileId, Pageable pageable);
    
    List<AdoptionInquiry> findByPetId(Long petId);
    
    List<AdoptionInquiry> findByStatus(AdoptionInquiry.InquiryStatus status);
    
    List<AdoptionInquiry> findByShelterProfileIdAndStatus(Long shelterProfileId, AdoptionInquiry.InquiryStatus status);
    
    @Query("SELECT a FROM AdoptionInquiry a WHERE a.shelterProfile.id = :shelterProfileId AND " +
           "(:petId IS NULL OR a.pet.id = :petId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:adopterName IS NULL OR LOWER(a.adopterName) LIKE LOWER(CONCAT('%', :adopterName, '%')))")
    Page<AdoptionInquiry> findInquiriesWithFilters(@Param("shelterProfileId") Long shelterProfileId,
                                                   @Param("petId") Long petId,
                                                   @Param("status") AdoptionInquiry.InquiryStatus status,
                                                   @Param("adopterName") String adopterName,
                                                   Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM AdoptionInquiry a WHERE a.shelterProfile.id = :shelterProfileId AND a.status = :status")
    Long countByShelterProfileIdAndStatus(@Param("shelterProfileId") Long shelterProfileId, 
                                        @Param("status") AdoptionInquiry.InquiryStatus status);
    
    Long countByShelterProfileId(Long shelterProfileId);
    
    List<AdoptionInquiry> findByAdopterEmailAndPetId(String adopterEmail, Long petId);
    
    boolean existsByAdopterEmailAndPetId(String adopterEmail, Long petId);
}
