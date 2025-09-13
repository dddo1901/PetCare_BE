package TechWiz.petOwner.controllers;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.PetGalleryRequest;
import TechWiz.petOwner.models.PetGallery;
import TechWiz.petOwner.services.PetGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pet-owner/gallery")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class PetGalleryController {

    @Autowired
    private PetGalleryService galleryService;

    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getGalleryByPet(@PathVariable Long petId, Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            List<PetGallery> gallery = galleryService.getGalleryByPet(petId, ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gallery images retrieved successfully",
                "data", gallery
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getGalleryByOwner(Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            List<PetGallery> gallery = galleryService.getGalleryByOwner(ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gallery images retrieved successfully",
                "data", gallery
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> createGalleryImage(@Valid @RequestBody PetGalleryRequest request, Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            PetGallery gallery = galleryService.createGalleryImage(request, ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gallery image created successfully",
                "data", gallery
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadGalleryImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("petId") Long petId,
            @RequestParam(value = "caption", required = false) String caption,
            Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "File is required"
                ));
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Only image files are allowed"
                ));
            }
            
            PetGallery gallery = galleryService.createGalleryImageWithFile(file, petId, ownerId, caption);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gallery image uploaded successfully",
                "data", gallery
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<?> updateGalleryImage(@PathVariable Long imageId, 
                                               @Valid @RequestBody PetGalleryRequest request, 
                                               Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            PetGallery gallery = galleryService.updateGalleryImage(imageId, request, ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Gallery image updated successfully",
                "data", gallery
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{imageId}/order")
    public ResponseEntity<?> updateDisplayOrder(@PathVariable Long imageId, 
                                               @RequestParam Integer displayOrder, 
                                               Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            PetGallery gallery = galleryService.updateDisplayOrder(imageId, displayOrder, ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Display order updated successfully",
                "data", gallery
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteGalleryImage(@PathVariable Long imageId, Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            boolean deleted = galleryService.deleteGalleryImage(imageId, ownerId);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Gallery image deleted successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/pet/{petId}")
    public ResponseEntity<?> deleteAllGalleryImagesByPet(@PathVariable Long petId, Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            galleryService.deleteAllGalleryImagesByPet(petId, ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "All gallery images deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/pet/{petId}/count")
    public ResponseEntity<?> getImageCountByPet(@PathVariable Long petId, Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            Long count = galleryService.getImageCountByPet(petId, ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Image count retrieved successfully",
                "data", Map.of("count", count, "maxImages", 10)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
