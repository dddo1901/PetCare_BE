package TechWiz.auths.models.dto;

import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Data;

@Data
public class UpdateShelterProfileRequest {
    private String address;
    private String shelterName;
    private String contactPersonName;
    private String registrationNumber;
    private String website;
    private String description;
    
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    private Integer currentOccupancy = 0;
    private List<String> images;
    private Boolean acceptsDonations = false;
    private String operatingHours;
    private String profileImageUrl;
}