package TechWiz.petOwner.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.petOwner.models.Vaccination;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    
    // Find vaccinations by pet
    List<Vaccination> findByPetIdOrderByDateDesc(Long petId);
    
    // Find vaccinations by pet and vaccine name
    List<Vaccination> findByPetIdAndNameContainingIgnoreCaseOrderByDateDesc(Long petId, String name);
    
    // Find upcoming vaccinations (due within next 30 days)
    @Query("SELECT v FROM Vaccination v WHERE v.pet.id = :petId AND " +
           "v.nextDue BETWEEN CURRENT_DATE AND :futureDate AND v.nextDue IS NOT NULL " +
           "ORDER BY v.nextDue ASC")
    List<Vaccination> findUpcomingVaccinations(@Param("petId") Long petId, @Param("futureDate") LocalDate futureDate);
    
    // Find overdue vaccinations
    @Query("SELECT v FROM Vaccination v WHERE v.pet.id = :petId AND " +
           "v.nextDue < CURRENT_DATE AND v.nextDue IS NOT NULL " +
           "ORDER BY v.nextDue ASC")
    List<Vaccination> findOverdueVaccinations(@Param("petId") Long petId);
    
    // Find all upcoming vaccinations for owner
    @Query("SELECT v FROM Vaccination v JOIN v.pet p WHERE p.ownerId = :ownerId AND " +
           "v.nextDue BETWEEN CURRENT_DATE AND :futureDate AND v.nextDue IS NOT NULL " +
           "ORDER BY v.nextDue ASC")
    List<Vaccination> findUpcomingVaccinationsByOwner(@Param("ownerId") Long ownerId, @Param("futureDate") LocalDate futureDate);
    
    // Find all overdue vaccinations for owner
    @Query("SELECT v FROM Vaccination v JOIN v.pet p WHERE p.ownerId = :ownerId AND " +
           "v.nextDue < CURRENT_DATE AND v.nextDue IS NOT NULL " +
           "ORDER BY v.nextDue ASC")
    List<Vaccination> findOverdueVaccinationsByOwner(@Param("ownerId") Long ownerId);
    
    // Find vaccinations by veterinarian name
    List<Vaccination> findByPetIdAndVetNameContainingIgnoreCaseOrderByDateDesc(Long petId, String vetName);
    
    // Find vaccinations by status
    List<Vaccination> findByPetIdAndStatusOrderByDateDesc(Long petId, String status);
    
    // Check if vaccination belongs to owner's pet
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vaccination v " +
           "JOIN v.pet p WHERE v.id = :vaccinationId AND p.ownerId = :ownerId")
    boolean existsByIdAndOwnerId(@Param("vaccinationId") Long vaccinationId, @Param("ownerId") Long ownerId);
    
    // Find vaccination by ID ensuring it belongs to owner
    @Query("SELECT v FROM Vaccination v JOIN v.pet p WHERE v.id = :vaccinationId AND p.ownerId = :ownerId")
    Optional<Vaccination> findByIdAndOwnerId(@Param("vaccinationId") Long vaccinationId, @Param("ownerId") Long ownerId);
    
    // Find latest vaccination by name for a pet
    @Query("SELECT v FROM Vaccination v WHERE v.pet.id = :petId AND v.name = :name " +
           "ORDER BY v.date DESC")
    Optional<Vaccination> findLatestByPetIdAndName(@Param("petId") Long petId, @Param("name") String name);
    
    // Count vaccinations by pet
    long countByPetId(Long petId);
    
    // Find vaccinations by date range
    @Query("SELECT v FROM Vaccination v WHERE v.pet.id = :petId AND " +
           "v.date BETWEEN :startDate AND :endDate ORDER BY v.date DESC")
    List<Vaccination> findByPetIdAndDateRange(@Param("petId") Long petId, 
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    // Find all vaccinations by owner
    @Query("SELECT v FROM Vaccination v JOIN v.pet p WHERE p.ownerId = :ownerId " +
           "ORDER BY v.date DESC")
    List<Vaccination> findByOwnerIdOrderByDateDesc(@Param("ownerId") Long ownerId);
    
    // Count overdue vaccinations by owner
    @Query("SELECT COUNT(v) FROM Vaccination v JOIN v.pet p WHERE p.ownerId = :ownerId AND " +
           "v.nextDue < CURRENT_DATE AND v.nextDue IS NOT NULL")
    long countOverdueVaccinationsByOwner(@Param("ownerId") Long ownerId);
    
    // Count upcoming vaccinations by owner
    @Query("SELECT COUNT(v) FROM Vaccination v JOIN v.pet p WHERE p.ownerId = :ownerId AND " +
           "v.nextDue BETWEEN CURRENT_DATE AND :futureDate AND v.nextDue IS NOT NULL")
    long countUpcomingVaccinationsByOwner(@Param("ownerId") Long ownerId, @Param("futureDate") LocalDate futureDate);
}
