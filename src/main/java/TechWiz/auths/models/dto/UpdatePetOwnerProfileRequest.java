package TechWiz.auths.models.dto;

import lombok.Data;

@Data
public class UpdatePetOwnerProfileRequest {
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String profileImageUrl;
    private String bio;
    private Boolean allowAccountSharing = false;
}