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
    
    List<CareLog> findByPetShelterIdAndType(Long shelterId, CareLog.CareType type);
    
    @Query("SELECT c FROM CareLog c WHERE c.pet.shelter.id = :shelterId AND " +
           "(:petId IS NULL OR c.pet.id = :petId) AND " +
           "(:type IS NULL OR c.type = :type) AND " +
           "(:staffName IS NULL OR LOWER(c.staffName) LIKE LOWER(CONCAT('%', :staffName, '%'))) AND " +
           "(:dateFrom IS NULL OR c.careDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR c.careDate <= :dateTo)")
    Page<CareLog> findCareLogsWithFilters(@Param("shelterId") Long shelterId,
                                         @Param("petId") Long petId,
                                         @Param("type") CareLog.CareType type,
                                         @Param("staffName") String staffName,
                                         @Param("dateFrom") LocalDateTime dateFrom,
                                         @Param("dateTo") LocalDateTime dateTo,
                                         Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM CareLog c WHERE c.pet.shelter.id = :shelterId AND " +
           "c.type = :type AND DATE(c.careDate) = CURRENT_DATE")
    Long countTodayLogsByShelterIdAndType(@Param("shelterId") Long shelterId, 
                                         @Param("type") CareLog.CareType type);
    
    @Query("SELECT c FROM CareLog c WHERE c.pet.id = :petId AND " +
           "DATE(c.careDate) = CURRENT_DATE ORDER BY c.careTime DESC")
    List<CareLog> findTodayLogsByPetId(@Param("petId") Long petId);
}
