package TechWiz.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelterBasicInfoDto {
    
    private Long id;
    private String shelterName;
    private String contactPersonName;
    private String email;
    private String phoneNumber;
    private String address;
    private String imageUrl;
}
