package TechWiz.petOwner.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.petOwner.dto.CreateMedicalRecordRequest;
import TechWiz.petOwner.models.MedicalRecord;
import TechWiz.petOwner.models.Pet;
import TechWiz.petOwner.repositories.MedicalRecordRepository;
import TechWiz.petOwner.repositories.PetRepository;

@Service
@Transactional
public class HealthRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private PetRepository petRepository;

    // Create new medical record
    public MedicalRecord createMedicalRecord(CreateMedicalRecordRequest request, Long ownerId) {
        // Verify pet belongs to owner
        Pet pet = petRepository.findByIdAndOwnerId(request.getPetId(), ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        MedicalRecord record = new MedicalRecord();
        record.setPet(pet);
        record.setVeterinarianId(request.getVeterinarianId());
        record.setVisitDate(request.getVisitDate());
        record.setRecordType(request.getRecordType());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setMedications(request.getMedications());
        record.setNotes(request.getNotes());
        record.setFollowUpRequired(request.getFollowUpRequired());
        record.setFollowUpDate(request.getFollowUpDate());
        record.setCost(request.getCost());
        
        // Update pet's last checkup date if this is a checkup
        if ("CHECKUP".equals(request.getRecordType()) || "ROUTINE".equals(request.getRecordType())) {
            pet.setLastCheckup(request.getVisitDate());
            petRepository.save(pet);
        }
        
        return medicalRecordRepository.save(record);
    }

    // Get medical records by pet
    @Transactional(readOnly = true)
    public List<MedicalRecord> getMedicalRecordsByPet(Long petId, Long ownerId) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        return medicalRecordRepository.findByPetIdOrderByVisitDateDesc(petId);
    }

    // Get medical records by pet with pagination
    @Transactional(readOnly = true)
    public Page<MedicalRecord> getMedicalRecordsByPet(Long petId, Long ownerId, Pageable pageable) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        return medicalRecordRepository.findByPetIdOrderByVisitDateDesc(petId, pageable);
    }

    // Get all medical records by owner
    @Transactional(readOnly = true)
    public List<MedicalRecord> getMedicalRecordsByOwner(Long ownerId) {
        return medicalRecordRepository.findByOwnerIdOrderByVisitDateDesc(ownerId);
    }

    // Get medical record by ID (with owner verification)
    @Transactional(readOnly = true)
    public Optional<MedicalRecord> getMedicalRecordById(Long recordId, Long ownerId) {
        return medicalRecordRepository.findByIdAndOwnerId(recordId, ownerId);
    }

    // Update medical record
    public MedicalRecord updateMedicalRecord(Long recordId, CreateMedicalRecordRequest request, Long ownerId) {
        MedicalRecord record = medicalRecordRepository.findByIdAndOwnerId(recordId, ownerId)
            .orElseThrow(() -> new RuntimeException("Medical record not found or doesn't belong to owner"));
        
        record.setVeterinarianId(request.getVeterinarianId());
        record.setVisitDate(request.getVisitDate());
        record.setRecordType(request.getRecordType());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setMedications(request.getMedications());
        record.setNotes(request.getNotes());
        record.setFollowUpRequired(request.getFollowUpRequired());
        record.setFollowUpDate(request.getFollowUpDate());
        record.setCost(request.getCost());
        
        return medicalRecordRepository.save(record);
    }

    // Delete medical record
    public boolean deleteMedicalRecord(Long recordId, Long ownerId) {
        if (medicalRecordRepository.existsByIdAndOwnerId(recordId, ownerId)) {
            medicalRecordRepository.deleteById(recordId);
            return true;
        }
        return false;
    }

    // Get medical records by type
    @Transactional(readOnly = true)
    public List<MedicalRecord> getMedicalRecordsByType(Long petId, String recordType, Long ownerId) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        return medicalRecordRepository.findByPetIdAndRecordTypeOrderByVisitDateDesc(petId, recordType);
    }

    // Get recent medical records (last 6 months)
    @Transactional(readOnly = true)
    public List<MedicalRecord> getRecentMedicalRecords(Long petId, Long ownerId) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return medicalRecordRepository.findRecentRecords(petId, sixMonthsAgo);
    }

    // Search medical records
    @Transactional(readOnly = true)
    public List<MedicalRecord> searchMedicalRecords(Long petId, String keyword, Long ownerId) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        return medicalRecordRepository.searchRecordsByPet(petId, keyword);
    }

    // Get medical records by date range
    @Transactional(readOnly = true)
    public List<MedicalRecord> getMedicalRecordsByDateRange(Long petId, LocalDate startDate, LocalDate endDate, Long ownerId) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        return medicalRecordRepository.findByPetIdAndDateRange(petId, startDate, endDate);
    }

    // Get records requiring follow-up
    @Transactional(readOnly = true)
    public List<MedicalRecord> getRecordsRequiringFollowUp(Long ownerId) {
        LocalDate today = LocalDate.now();
        return medicalRecordRepository.findByOwnerIdOrderByVisitDateDesc(ownerId)
            .stream()
            .filter(record -> record.getFollowUpRequired() && 
                            record.getFollowUpDate() != null && 
                            !record.getFollowUpDate().isAfter(today))
            .toList();
    }

    // Count medical records by pet
    @Transactional(readOnly = true)
    public long getMedicalRecordCountByPet(Long petId, Long ownerId) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        return medicalRecordRepository.countByPetId(petId);
    }

    // Get health summary for pet
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getHealthSummary(Long petId, Long ownerId) {
        // Verify pet belongs to owner
        Pet pet = petRepository.findByIdAndOwnerId(petId, ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        List<MedicalRecord> records = medicalRecordRepository.findByPetIdOrderByVisitDateDesc(petId);
        
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("pet", pet);
        summary.put("totalRecords", records.size());
        summary.put("lastCheckup", pet.getLastCheckup());
        summary.put("nextVaccination", pet.getNextVaccination());
        summary.put("healthStatus", pet.getHealthStatus());
        
        // Recent records (last 3)
        List<MedicalRecord> recentRecords = records.stream().limit(3).toList();
        summary.put("recentRecords", recentRecords);
        
        // Records by type
        java.util.Map<String, Long> recordsByType = records.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                MedicalRecord::getRecordType, 
                java.util.stream.Collectors.counting()
            ));
        summary.put("recordsByType", recordsByType);
        
        return summary;
    }

    // Check if medical record belongs to owner
    @Transactional(readOnly = true)
    public boolean isMedicalRecordOwnedBy(Long recordId, Long ownerId) {
        return medicalRecordRepository.existsByIdAndOwnerId(recordId, ownerId);
    }
}
