package TechWiz.petOwner.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePetRequest {
    
    @NotBlank(message = "Pet name is required")
    private String name;
    
    @NotBlank(message = "Pet type is required")
    private String type;
    
    private String breed;
    private String age;
    private String gender;
    private String weight;
    private String color;
    private String microchipId;
    private String imageUrl;
    private String description;
    private String healthStatus;
    private LocalDate lastCheckup;
    private LocalDate nextVaccination;
    private String vetName;
    private String vetPhone;
    private String insuranceProvider;
    private String policyNumber;
    private String emergencyContact;
    private String emergencyPhone;
    
    // Gallery photos as URLs
    private List<String> galleryPhotos;
}
