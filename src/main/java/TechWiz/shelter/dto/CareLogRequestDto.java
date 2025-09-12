package TechWiz.shelter.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import TechWiz.shelter.models.CareLog;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareLogRequestDto {
    
    @NotNull(message = "Care type is required")
    private CareLog.CareType type;
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotNull(message = "Care date is required")
    private LocalDateTime careDate;
    
    @NotNull(message = "Care time is required")
    private LocalTime careTime;
    
    @NotBlank(message = "Details are required")
    @Size(max = 2000, message = "Details must not exceed 2000 characters")
    private String details;
    
    @NotBlank(message = "Staff name is required")
    @Size(max = 100, message = "Staff name must not exceed 100 characters")
    private String staffName;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    private List<String> attachments;
}
