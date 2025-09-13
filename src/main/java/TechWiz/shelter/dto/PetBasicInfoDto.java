package TechWiz.shelter.dto;

import TechWiz.shelter.models.ShelterPet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetBasicInfoDto {
    
    private Long id;
    private String name;
    private ShelterPet.PetType type;
    private String breed;
    private String imageUrl;
}
