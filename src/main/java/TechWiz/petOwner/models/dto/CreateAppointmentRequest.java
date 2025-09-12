package TechWiz.petOwner.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotBlank(message = "Appointment type is required")
    private String type; // General Checkup, Vaccination, Treatment
    
    private String reason;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Time is required")
    private LocalTime time;
    
    @NotBlank(message = "Vet name is required")
    private String vetName;
    
    private String vetSpecialty;
    private String vetAvatar;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String vetPhone;
    private BigDecimal fee;
    private String notes;
}
