package TechWiz.veterinarian.services;

import TechWiz.petOwner.models.PetOwnerHealthRecord;
import TechWiz.petOwner.models.PetOwnerPet;
import TechWiz.petOwner.repositories.PetOwnerHealthRecordRepository;
import TechWiz.petOwner.repositories.PetOwnerPetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VeterinarianHealthRecordService {

    @Autowired
    private PetOwnerHealthRecordRepository healthRecordRepository;

    @Autowired
    private PetOwnerPetRepository petRepository;

    public Page<Map<String, Object>> getHealthRecordsByPet(Long petId, Pageable pageable) {
        try {
            List<PetOwnerHealthRecord> records = healthRecordRepository.findByPetId(petId);
            
            // Transform to Map for frontend
            List<Map<String, Object>> transformedRecords = records.stream()
                .map(this::transformHealthRecordToMap)
                .collect(Collectors.toList());

            // Apply pagination manually
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), transformedRecords.size());
            List<Map<String, Object>> pageContent = transformedRecords.subList(start, end);

            return new PageImpl<>(pageContent, pageable, transformedRecords.size());
        } catch (Exception e) {
            System.err.println("Error retrieving health records for pet " + petId + ": " + e.getMessage());
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    public Map<String, Object> getHealthRecordStatistics(Long petId) {
        try {
            List<PetOwnerHealthRecord> records = healthRecordRepository.findByPetId(petId);
            
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
        } catch (Exception e) {
            System.err.println("Error retrieving statistics for pet " + petId + ": " + e.getMessage());
            return Map.of(
                "totalRecords", 0,
                "medicalHistory", 0,
                "vaccinations", 0,
                "medications", 0
            );
        }
    }

    public Map<String, Object> getPetInfo(Long petId) {
        try {
            Optional<PetOwnerPet> petOpt = petRepository.findById(petId);
            if (petOpt.isPresent()) {
                PetOwnerPet pet = petOpt.get();
                Map<String, Object> petInfo = new HashMap<>();
                petInfo.put("id", pet.getId());
                petInfo.put("name", pet.getName());
                petInfo.put("species", pet.getType().getDisplayName());
                petInfo.put("breed", pet.getBreed());
                petInfo.put("age", convertAgeToString(pet.getAgeInMonths()));
                petInfo.put("gender", pet.getGender());
                petInfo.put("weight", pet.getWeight());
                
                // Handle image URL
                String imageUrl = pet.getImageUrl();
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    petInfo.put("imageUrl", imageUrl);
                } else {
                    // Try to get from photos array
                    String photos = pet.getPhotos();
                    if (photos != null && !photos.trim().isEmpty()) {
                        try {
                            if (photos.startsWith("[")) {
                                String cleanPhotos = photos.substring(1, photos.length() - 1);
                                String[] photoArray = cleanPhotos.split(",");
                                if (photoArray.length > 0) {
                                    String firstPhoto = photoArray[0].trim().replaceAll("^\"|\"$", "");
                                    petInfo.put("imageUrl", firstPhoto);
                                }
                            } else {
                                String[] photoArray = photos.split(",");
                                if (photoArray.length > 0) {
                                    String firstPhoto = photoArray[0].trim().replaceAll("^\"|\"$", "");
                                    petInfo.put("imageUrl", firstPhoto);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing pet photos: " + e.getMessage());
                        }
                    }
                }
                
                return petInfo;
            } else {
                throw new RuntimeException("Pet not found with ID: " + petId);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving pet info for pet " + petId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve pet information: " + e.getMessage());
        }
    }

    private Map<String, Object> transformHealthRecordToMap(PetOwnerHealthRecord record) {
        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("id", record.getId());
        recordMap.put("petId", record.getPetId());
        recordMap.put("type", record.getType());
        recordMap.put("description", record.getDescription());
        recordMap.put("vet", record.getVet());
        recordMap.put("clinic", record.getClinic());
        recordMap.put("cost", record.getCost());
        recordMap.put("notes", record.getNotes());
        recordMap.put("recordDate", record.getRecordDate());
        recordMap.put("createdAt", record.getCreatedAt());
        recordMap.put("updatedAt", record.getUpdatedAt());
        return recordMap;
    }

    private String convertAgeToString(Integer ageInMonths) {
        if (ageInMonths == null) return "Unknown";
        if (ageInMonths < 12) {
            return ageInMonths + " months";
        } else {
            int years = ageInMonths / 12;
            int months = ageInMonths % 12;
            if (months == 0) {
                return years + " years";
            } else {
                return years + " years " + months + " months";
            }
        }
    }
}
