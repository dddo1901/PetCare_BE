package TechWiz.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import TechWiz.shelter.models.AdoptionInquiry;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseRequestDto {
    
    @NotNull(message = "Status is required")
    private AdoptionInquiry.InquiryStatus status;
    
    @NotBlank(message = "Response message is required")
    @Size(max = 2000, message = "Response message must not exceed 2000 characters")
    private String shelterResponse;
}
