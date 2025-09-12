package TechWiz.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import TechWiz.shelter.models.Pet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetBasicInfoDto {
    
    private Long id;
    private String name;
    private Pet.PetType type;
    private String breed;
    private String imageUrl;
}
