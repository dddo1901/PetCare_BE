package TechWiz.shelter.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import TechWiz.shelter.models.CareLog;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareLogResponseDto {
    
    private Long id;
    private CareLog.CareType type;
    private LocalDateTime careDate;
    private LocalTime careTime;
    private String details;
    private String staffName;
    private String notes;
    private List<String> attachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Pet basic info
    private PetBasicInfoDto pet;
}
