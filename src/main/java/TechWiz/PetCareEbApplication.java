package TechWiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {}, 
    scanBasePackages = {"TechWiz.auths", "TechWiz.petOwner", "TechWiz.shelter", "TechWiz.veterinarian", "TechWiz.admin", "TechWiz.common"})
@EnableJpaRepositories
public class PetCareEbApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetCareEbApplication.class, args);
	}

}
