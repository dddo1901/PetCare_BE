package TechWiz.shelter.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.shelter.models.CareLog;

@Repository
public interface CareLogRepository extends JpaRepository<CareLog, Long> {
    
    List<CareLog> findByPetId(Long petId);
    
    Page<CareLog> findByPetId(Long petId, Pageable pageable);
    
    List<CareLog> findByPetIdAndType(Long petId, CareLog.CareType type);
    
    List<CareLog> findByPetShelterProfileIdAndType(Long shelterProfileId, CareLog.CareType type);
    
    @Query("SELECT c FROM CareLog c WHERE c.pet.shelterProfile.id = :shelterProfileId AND " +
           "(:petId IS NULL OR c.pet.id = :petId) AND " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:staffName IS NULL OR LOWER(c.staffName) LIKE LOWER(CONCAT('%', :staffName, '%'))) AND " +
           "(:dateFrom IS NULL OR c.careDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.careDate <= :dateTo)")
    Page<CareLog> findCareLogsWithFilters(@Param("shelterProfileId") Long shelterProfileId,
                                         @Param("petId") Long petId,
                                         @Param("type") CareLog.CareType type,
                                         @Param("staffName") String staffName,
                                         @Param("dateFrom") LocalDateTime dateFrom,
                                         @Param("dateTo") LocalDateTime dateTo,
                                         Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM CareLog c WHERE c.pet.shelterProfile.id = :shelterProfileId AND " +
           "c.type = :type AND DATE(c.careDate) = CURRENT_DATE")
    Long countTodayLogsByShelterIdAndType(@Param("shelterProfileId") Long shelterProfileId, 
                                         @Param("type") CareLog.CareType type);
    
    @Query("SELECT c FROM CareLog c WHERE c.pet.id = :petId AND " +
           "DATE(c.careDate) = CURRENT_DATE ORDER BY c.careTime DESC")
    List<CareLog> findTodayLogsByPetId(@Param("petId") Long petId);
    
    @Query("SELECT c FROM CareLog c WHERE c.pet.shelterProfile.id = :shelterProfileId " +
           "ORDER BY c.careDate DESC, c.careTime DESC")
    List<CareLog> findRecentLogsByShelter(@Param("shelterProfileId") Long shelterProfileId, 
                                         Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM CareLog c WHERE c.pet.shelterProfile.id = :shelterProfileId AND " +
           "c.type = :type AND " +
           "(:dateFrom IS NULL OR c.careDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.careDate <= :dateTo)")
    Long countLogsByShelterAndTypeInDateRange(@Param("shelterProfileId") Long shelterProfileId,
                                             @Param("type") CareLog.CareType type,
                                             @Param("dateFrom") LocalDateTime dateFrom,
                                             @Param("dateTo") LocalDateTime dateTo);
    
    @Query("SELECT COUNT(c) FROM CareLog c WHERE c.pet.shelterProfile.id = :shelterProfileId AND " +
           "(:dateFrom IS NULL OR c.careDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.careDate <= :dateTo)")
    Long countLogsByShelterInDateRange(@Param("shelterProfileId") Long shelterProfileId,
                                      @Param("dateFrom") LocalDateTime dateFrom,
                                      @Param("dateTo") LocalDateTime dateTo);
    
    @Query("SELECT p.name, COUNT(c) FROM CareLog c JOIN c.pet p " +
           "WHERE p.shelterProfile.id = :shelterProfileId AND " +
           "(:dateFrom IS NULL OR c.careDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.careDate <= :dateTo) " +
           "GROUP BY p.id, p.name ORDER BY COUNT(c) DESC")
    List<Object[]> findMostActivePetsByShelterId(@Param("shelterProfileId") Long shelterProfileId,
                                                @Param("dateFrom") LocalDateTime dateFrom,
                                                @Param("dateTo") LocalDateTime dateTo,
                                                Pageable pageable);
}
