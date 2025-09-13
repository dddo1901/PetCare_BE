package TechWiz.shelter.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.shelter.dto.CareLogRequestDto;
import TechWiz.shelter.dto.CareLogResponseDto;
import TechWiz.shelter.models.CareLog;
import TechWiz.shelter.models.Pet;
import TechWiz.shelter.repositories.CareLogRepository;
import TechWiz.shelter.repositories.ShelterPetRepository;

@Service
@Transactional
public class CareLogService {
    
    @Autowired
    private CareLogRepository careLogRepository;
    
    @Autowired
    private ShelterPetRepository petRepository;
    
    @Autowired
    private ShelterPetService petService;
    
    public CareLogResponseDto createCareLog(Long shelterProfileId, CareLogRequestDto requestDto) {
        Pet pet = petRepository.findById(requestDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + requestDto.getPetId()));
        
        // Verify pet belongs to the shelter profile
        if (!pet.getShelterProfile().getId().equals(shelterProfileId)) {
            throw new RuntimeException("Pet does not belong to the specified shelter");
        }
        
        CareLog careLog = new CareLog();
        careLog.setType(requestDto.getType());
        careLog.setPet(pet);
        careLog.setCareDate(requestDto.getCareDate());
        careLog.setCareTime(requestDto.getCareTime());
        careLog.setDetails(requestDto.getDetails());
        careLog.setStaffName(requestDto.getStaffName());
        careLog.setNotes(requestDto.getNotes());
        careLog.setAttachments(requestDto.getAttachments());
        
        CareLog savedCareLog = careLogRepository.save(careLog);
        return convertToResponseDto(savedCareLog);
    }
    
    public CareLogResponseDto getCareLogById(Long id) {
        CareLog careLog = careLogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Care log not found with id: " + id));
        return convertToResponseDto(careLog);
    }
    
    public Page<CareLogResponseDto> getCareLogsByShelterId(Long shelterProfileId,
                                                          Long petId,
                                                          CareLog.CareType type,
                                                          String staffName,
                                                          LocalDateTime dateFrom,
                                                          LocalDateTime dateTo,
                                                          Pageable pageable) {
        Page<CareLog> careLogs = careLogRepository.findCareLogsWithFilters(
            shelterProfileId, petId, type, staffName, dateFrom, dateTo, pageable);
        
        List<CareLogResponseDto> dtos = careLogs.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(dtos, pageable, careLogs.getTotalElements());
    }
    
    public List<CareLogResponseDto> getCareLogsByPetId(Long petId) {
        List<CareLog> careLogs = careLogRepository.findByPetId(petId);
        return careLogs.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public List<CareLogResponseDto> getCareLogsByPetIdAndType(Long petId, CareLog.CareType type) {
        List<CareLog> careLogs = careLogRepository.findByPetIdAndType(petId, type);
        return careLogs.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public List<CareLogResponseDto> getTodayCareLogsByPetId(Long petId) {
        List<CareLog> careLogs = careLogRepository.findTodayLogsByPetId(petId);
        return careLogs.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public Long getTodayCareLogsCountByShelterIdAndType(Long shelterId, CareLog.CareType type) {
        return careLogRepository.countTodayLogsByShelterIdAndType(shelterId, type);
    }
    
    public CareLogResponseDto updateCareLog(Long id, CareLogRequestDto requestDto) {
        CareLog careLog = careLogRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Care log not found with id: " + id));
        
        // Verify pet exists and belongs to the same shelter profile
        Pet pet = petRepository.findById(requestDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + requestDto.getPetId()));
        
        if (!pet.getShelterProfile().getId().equals(careLog.getPet().getShelterProfile().getId())) {
            throw new RuntimeException("Pet does not belong to the same shelter");
        }
        
        careLog.setType(requestDto.getType());
        careLog.setPet(pet);
        careLog.setCareDate(requestDto.getCareDate());
        careLog.setCareTime(requestDto.getCareTime());
        careLog.setDetails(requestDto.getDetails());
        careLog.setStaffName(requestDto.getStaffName());
        careLog.setNotes(requestDto.getNotes());
        careLog.setAttachments(requestDto.getAttachments());
        careLog.setUpdatedAt(LocalDateTime.now());
        
        CareLog updatedCareLog = careLogRepository.save(careLog);
        return convertToResponseDto(updatedCareLog);
    }
    
    public void deleteCareLog(Long id) {
        if (!careLogRepository.existsById(id)) {
            throw new RuntimeException("Care log not found with id: " + id);
        }
        careLogRepository.deleteById(id);
    }
    
    private CareLogResponseDto convertToResponseDto(CareLog careLog) {
        CareLogResponseDto dto = new CareLogResponseDto();
        dto.setId(careLog.getId());
        dto.setType(careLog.getType());
        dto.setCareDate(careLog.getCareDate());
        dto.setCareTime(careLog.getCareTime());
        dto.setDetails(careLog.getDetails());
        dto.setStaffName(careLog.getStaffName());
        dto.setNotes(careLog.getNotes());
        dto.setAttachments(careLog.getAttachments());
        dto.setCreatedAt(careLog.getCreatedAt());
        dto.setUpdatedAt(careLog.getUpdatedAt());
        
        // Set pet info
        dto.setPet(petService.convertToBasicInfoDto(careLog.getPet()));
        
        return dto;
    }
    
    public List<CareLogResponseDto> getRecentCareLogsByShelterId(Long shelterId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<CareLog> careLogs = careLogRepository.findRecentLogsByShelter(shelterId, pageable);
        return careLogs.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public Map<String, Object> getCareLogsSummary(Long shelterId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        Map<String, Object> summary = new HashMap<>();
        
        // Total counts by type
        Map<String, Long> typeCounts = new HashMap<>();
        for (CareLog.CareType type : CareLog.CareType.values()) {
            Long count = careLogRepository.countLogsByShelterAndTypeInDateRange(shelterId, type, dateFrom, dateTo);
            typeCounts.put(type.toString().toLowerCase(), count);
        }
        summary.put("byType", typeCounts);
        
        // Total count
        Long totalCount = careLogRepository.countLogsByShelterInDateRange(shelterId, dateFrom, dateTo);
        summary.put("total", totalCount);
        
        // Most active pets
        Pageable pageable = PageRequest.of(0, 5);
        List<Object[]> activePets = careLogRepository.findMostActivePetsByShelterId(shelterId, dateFrom, dateTo, pageable);
        summary.put("mostActivePets", activePets);
        
        return summary;
    }
}
