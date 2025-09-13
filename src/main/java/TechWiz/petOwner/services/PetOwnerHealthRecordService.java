package TechWiz.petOwner.services;

import TechWiz.petOwner.dto.PetOwnerHealthRecordRequest;
import TechWiz.petOwner.models.PetOwnerHealthRecord;
import TechWiz.petOwner.repositories.PetOwnerHealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PetOwnerHealthRecordService {
    
    @Autowired
    private PetOwnerHealthRecordRepository healthRecordRepository;
    
    public PetOwnerHealthRecord createHealthRecord(PetOwnerHealthRecordRequest request, Long ownerId) {
        PetOwnerHealthRecord record = new PetOwnerHealthRecord();
        record.setPetId(request.getPetId());
        record.setOwnerId(ownerId);
        record.setType(request.getType());
        record.setDescription(request.getDescription());
        record.setVet(request.getVet());
        record.setClinic(request.getClinic());
        record.setCost(request.getCost());
        record.setNotes(request.getNotes());
        record.setRecordDate(request.getRecordDate() != null ? request.getRecordDate() : LocalDateTime.now());
        
        return healthRecordRepository.save(record);
    }
    
    public Map<String, Object> getCombinedHealthData(Long petId, Long ownerId) {
        List<PetOwnerHealthRecord> records = healthRecordRepository.findByPetIdAndOwnerId(petId, ownerId);
        
        Map<String, Object> result = new HashMap<>();
        
        // Group records by type
        List<Map<String, Object>> medicalHistory = new ArrayList<>();
        List<Map<String, Object>> vaccinations = new ArrayList<>();
        List<Map<String, Object>> medications = new ArrayList<>();
        
        for (PetOwnerHealthRecord record : records) {
            Map<String, Object> recordData = new HashMap<>();
            recordData.put("id", record.getId());
            recordData.put("type", record.getType());
            recordData.put("description", record.getDescription());
            recordData.put("vet", record.getVet());
            recordData.put("clinic", record.getClinic());
            recordData.put("cost", record.getCost());
            recordData.put("notes", record.getNotes());
            recordData.put("recordDate", record.getRecordDate());
            recordData.put("createdAt", record.getCreatedAt());
            
            String type = record.getType().toLowerCase();
            if (type.contains("vaccination") || type.contains("vaccine")) {
                vaccinations.add(recordData);
            } else if (type.contains("medication") || type.contains("medicine")) {
                medications.add(recordData);
            } else {
                medicalHistory.add(recordData);
            }
        }
        
        result.put("medicalHistory", medicalHistory);
        result.put("vaccinations", vaccinations);
        result.put("medications", medications);
        result.put("insurance", null); // Placeholder for insurance data
        
        return result;
    }
    
    public Map<String, Object> getHealthRecordStatistics(Long petId, Long ownerId) {
        List<PetOwnerHealthRecord> records = healthRecordRepository.findByPetIdAndOwnerId(petId, ownerId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", records.size());
        
        // Count by type
        long medicalHistoryCount = records.stream()
            .filter(record -> {
                String type = record.getType().toLowerCase();
                return !type.contains("vaccination") && !type.contains("vaccine") && 
                       !type.contains("medication") && !type.contains("medicine");
            })
            .count();
        
        long vaccinationCount = records.stream()
            .filter(record -> {
                String type = record.getType().toLowerCase();
                return type.contains("vaccination") || type.contains("vaccine");
            })
            .count();
        
        long medicationCount = records.stream()
            .filter(record -> {
                String type = record.getType().toLowerCase();
                return type.contains("medication") || type.contains("medicine");
            })
            .count();
        
        stats.put("medicalHistory", medicalHistoryCount);
        stats.put("vaccinations", vaccinationCount);
        stats.put("medications", medicationCount);
        
        return stats;
    }
    
    public List<PetOwnerHealthRecord> getHealthRecordsByPet(Long petId, Long ownerId) {
        return healthRecordRepository.findByPetIdAndOwnerId(petId, ownerId);
    }
    
    public Page<PetOwnerHealthRecord> getHealthRecordsByPet(Long petId, Long ownerId, Pageable pageable) {
        return healthRecordRepository.findByPetIdAndOwnerId(petId, ownerId, pageable);
    }
    
    public List<PetOwnerHealthRecord> getHealthRecordsByOwner(Long ownerId) {
        return healthRecordRepository.findByOwnerId(ownerId);
    }
    
    public Page<PetOwnerHealthRecord> getHealthRecordsByOwner(Long ownerId, Pageable pageable) {
        return healthRecordRepository.findByOwnerId(ownerId, pageable);
    }
    
    public List<PetOwnerHealthRecord> getHealthRecordsByType(Long petId, String type) {
        return healthRecordRepository.findByPetIdAndType(petId, type);
    }
    
    public List<PetOwnerHealthRecord> getHealthRecordsByDateRange(Long petId, LocalDateTime start, LocalDateTime end) {
        return healthRecordRepository.findByPetIdAndRecordDateBetween(petId, start, end);
    }
    
    public Long getHealthRecordCountByPet(Long petId) {
        return healthRecordRepository.countByPetId(petId);
    }
    
    public Long getHealthRecordCountByOwner(Long ownerId) {
        return healthRecordRepository.countByOwnerId(ownerId);
    }
    
    public Long getHealthRecordCountByPetAndType(Long petId, String type) {
        return healthRecordRepository.countByPetIdAndType(petId, type);
    }
    
    public PetOwnerHealthRecord updateHealthRecord(Long recordId, PetOwnerHealthRecordRequest request, Long ownerId) {
        PetOwnerHealthRecord record = healthRecordRepository.findById(recordId)
                .filter(r -> r.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Health record not found"));
        
        record.setType(request.getType());
        record.setDescription(request.getDescription());
        record.setVet(request.getVet());
        record.setClinic(request.getClinic());
        record.setCost(request.getCost());
        record.setNotes(request.getNotes());
        record.setRecordDate(request.getRecordDate());
        
        return healthRecordRepository.save(record);
    }
    
    public boolean deleteHealthRecord(Long recordId, Long ownerId) {
        Optional<PetOwnerHealthRecord> record = healthRecordRepository.findById(recordId)
                .filter(r -> r.getOwnerId().equals(ownerId));
        
        if (record.isPresent()) {
            healthRecordRepository.delete(record.get());
            return true;
        }
        return false;
    }
}
