package TechWiz.petOwner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import TechWiz.petOwner.models.*;
import TechWiz.petOwner.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/test-data")
@CrossOrigin(origins = "*")
public class TestDataController {

    @Autowired
    private PetOwnerPetRepository petRepository;

    @Autowired
    private PetOwnerAppointmentRepository appointmentRepository;

    @Autowired
    private PetOwnerHealthRecordRepository healthRecordRepository;

    @Autowired
    private PetGalleryRepository galleryRepository;

    @PostMapping("/create-sample-data")
    public ResponseEntity<?> createSampleData() {
        try {
            Long ownerId = 3L; // Test owner ID

            // Create sample pets
            PetOwnerPet pet1 = new PetOwnerPet();
            pet1.setOwnerId(ownerId);
            pet1.setName("Buddy");
            pet1.setType(PetOwnerPet.PetType.DOG);
            pet1.setBreed("Golden Retriever");
            pet1.setAgeInMonths(24);
            pet1.setGender(PetOwnerPet.Gender.MALE);
            pet1.setColor("Golden");
            pet1.setWeight(new BigDecimal("25.5"));
            pet1.setHealthStatus(PetOwnerPet.HealthStatus.HEALTHY);
            pet1.setVaccinated(true);
            pet1.setSpayedNeutered(false);
            pet1.setMicrochipped(true);
            pet1.setHouseTrained(true);
            pet1.setGoodWithKids(true);
            pet1.setGoodWithPets(true);
            pet1.setEnergyLevel(PetOwnerPet.EnergyLevel.MEDIUM);
            pet1.setDescription("Friendly and energetic dog");
            pet1.setCreatedAt(LocalDateTime.now());
            pet1.setUpdatedAt(LocalDateTime.now());
            petRepository.save(pet1);

            PetOwnerPet pet2 = new PetOwnerPet();
            pet2.setOwnerId(ownerId);
            pet2.setName("Whiskers");
            pet2.setType(PetOwnerPet.PetType.CAT);
            pet2.setBreed("Persian");
            pet2.setAgeInMonths(18);
            pet2.setGender(PetOwnerPet.Gender.FEMALE);
            pet2.setColor("White");
            pet2.setWeight(new BigDecimal("4.2"));
            pet2.setHealthStatus(PetOwnerPet.HealthStatus.HEALTHY);
            pet2.setVaccinated(true);
            pet2.setSpayedNeutered(true);
            pet2.setMicrochipped(false);
            pet2.setHouseTrained(true);
            pet2.setGoodWithKids(false);
            pet2.setGoodWithPets(true);
            pet2.setEnergyLevel(PetOwnerPet.EnergyLevel.LOW);
            pet2.setDescription("Calm and independent cat");
            pet2.setCreatedAt(LocalDateTime.now());
            pet2.setUpdatedAt(LocalDateTime.now());
            petRepository.save(pet2);

            // Create sample appointments
            PetOwnerAppointment appointment1 = new PetOwnerAppointment();
            appointment1.setOwnerId(ownerId);
            appointment1.setPetId(pet1.getId());
            appointment1.setVetId(2L);
            appointment1.setType("Regular Checkup");
            appointment1.setReason("Annual health check");
            appointment1.setAppointmentDateTime(LocalDateTime.now().plusDays(3));
            appointment1.setStatus(PetOwnerAppointment.AppointmentStatus.CONFIRMED);
            appointment1.setCreatedAt(LocalDateTime.now());
            appointmentRepository.save(appointment1);

            PetOwnerAppointment appointment2 = new PetOwnerAppointment();
            appointment2.setOwnerId(ownerId);
            appointment2.setPetId(pet2.getId());
            appointment2.setVetId(2L);
            appointment2.setType("Vaccination");
            appointment2.setReason("Annual vaccination");
            appointment2.setAppointmentDateTime(LocalDateTime.now().plusDays(7));
            appointment2.setStatus(PetOwnerAppointment.AppointmentStatus.PENDING);
            appointment2.setCreatedAt(LocalDateTime.now());
            appointmentRepository.save(appointment2);

            // Create sample health records
            PetOwnerHealthRecord record1 = new PetOwnerHealthRecord();
            record1.setPetId(pet1.getId());
            record1.setOwnerId(ownerId);
            record1.setType("Vaccination");
            record1.setDescription("Annual vaccination completed");
            record1.setVet("Dr. Smith");
            record1.setClinic("Pet Care Clinic");
            record1.setCost(new BigDecimal("150.00"));
            record1.setNotes("All vaccinations up to date");
            record1.setRecordDate(LocalDateTime.now().minusDays(30));
            record1.setCreatedAt(LocalDateTime.now());
            healthRecordRepository.save(record1);

            PetOwnerHealthRecord record2 = new PetOwnerHealthRecord();
            record2.setPetId(pet2.getId());
            record2.setOwnerId(ownerId);
            record2.setType("Checkup");
            record2.setDescription("Regular health checkup");
            record2.setVet("Dr. Johnson");
            record2.setClinic("Animal Hospital");
            record2.setCost(new BigDecimal("80.00"));
            record2.setNotes("Pet is healthy");
            record2.setRecordDate(LocalDateTime.now().minusDays(15));
            record2.setCreatedAt(LocalDateTime.now());
            healthRecordRepository.save(record2);

            // Create sample gallery images
            PetGallery gallery1 = new PetGallery();
            gallery1.setPetId(pet1.getId());
            gallery1.setOwnerId(ownerId);
            gallery1.setImageUrl("http://localhost:8080/api/pet-owner/photos/sample1.jpg");
            gallery1.setDisplayOrder(1);
            gallery1.setCreatedAt(LocalDateTime.now());
            galleryRepository.save(gallery1);

            PetGallery gallery2 = new PetGallery();
            gallery2.setPetId(pet1.getId());
            gallery2.setOwnerId(ownerId);
            gallery2.setImageUrl("http://localhost:8080/api/pet-owner/photos/sample2.jpg");
            gallery2.setDisplayOrder(2);
            gallery2.setCreatedAt(LocalDateTime.now());
            galleryRepository.save(gallery2);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sample data created successfully",
                "data", Map.of(
                    "pets", 2,
                    "appointments", 2,
                    "healthRecords", 2,
                    "galleryImages", 2
                )
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error creating sample data: " + e.getMessage()
            ));
        }
    }
}
