package TechWiz.shelter.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.shelter.models.CareLog;
import TechWiz.shelter.models.Pet;
import TechWiz.shelter.repositories.CareLogRepository;
import TechWiz.shelter.repositories.ShelterPetRepository;
import TechWiz.shelter.dto.*;

@Service
@Transactional
public class CareLogService {
    
    @Autowired
    private CareLogRepository careLogRepository;
    
    @Autowired
    private ShelterPetRepository petRepository;
    
    @Autowired
    private ShelterPetService petService;
    
    public CareLogResponseDto createCareLog(Long shelterId, CareLogRequestDto requestDto) {
        Pet pet = petRepository.findById(requestDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + requestDto.getPetId()));
        
        // Verify pet belongs to the shelter
        if (!pet.getShelter().getId().equals(shelterId)) {
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
    
    public Page<CareLogResponseDto> getCareLogsByShelterId(Long shelterId,
                                                          Long petId,
                                                          CareLog.CareType type,
                                                          String staffName,
                                                          LocalDateTime dateFrom,
                                                          LocalDateTime dateTo,
                                                          Pageable pageable) {
        Page<CareLog> careLogs = careLogRepository.findCareLogsWithFilters(
            shelterId, petId, type, staffName, dateFrom, dateTo, pageable);
        
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
        
        // Verify pet exists and belongs to the same shelter
        Pet pet = petRepository.findById(requestDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + requestDto.getPetId()));
        
        if (!pet.getShelter().getId().equals(careLog.getPet().getShelter().getId())) {
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
}
