package TechWiz.petOwner.services;

import TechWiz.petOwner.dto.PetGalleryRequest;
import TechWiz.petOwner.models.PetGallery;
import TechWiz.petOwner.repositories.PetGalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PetGalleryService {
    
    @Autowired
    private PetGalleryRepository galleryRepository;
    
    private static final String UPLOAD_DIR = "uploads/pet-gallery/";
    private static final int MAX_IMAGES_PER_PET = 10;
    
    public PetGallery createGalleryImage(PetGalleryRequest request, Long ownerId) {
        // Check if pet already has maximum images
        Long currentCount = galleryRepository.countByPetIdAndOwnerId(request.getPetId(), ownerId);
        if (currentCount >= MAX_IMAGES_PER_PET) {
            throw new RuntimeException("Maximum " + MAX_IMAGES_PER_PET + " images allowed per pet");
        }
        
        PetGallery gallery = new PetGallery();
        gallery.setPetId(request.getPetId());
        gallery.setOwnerId(ownerId);
        gallery.setImageUrl(request.getImageUrl());
        gallery.setCaption(request.getCaption());
        gallery.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        
        return galleryRepository.save(gallery);
    }
    
    public PetGallery createGalleryImageWithFile(MultipartFile file, Long petId, Long ownerId, String caption) {
        // Check if pet already has maximum images
        Long currentCount = galleryRepository.countByPetIdAndOwnerId(petId, ownerId);
        if (currentCount >= MAX_IMAGES_PER_PET) {
            throw new RuntimeException("Maximum " + MAX_IMAGES_PER_PET + " images allowed per pet");
        }
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = "pet_" + petId + "_" + UUID.randomUUID().toString() + extension;
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            
            // Create gallery record
            PetGallery gallery = new PetGallery();
            gallery.setPetId(petId);
            gallery.setOwnerId(ownerId);
            gallery.setImageUrl("/api/pet-owner/gallery/images/" + filename);
            gallery.setCaption(caption);
            gallery.setDisplayOrder(0);
            
            return galleryRepository.save(gallery);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }
    
    public List<PetGallery> getGalleryByPet(Long petId, Long ownerId) {
        return galleryRepository.findByPetIdAndOwnerIdOrderByDisplayOrderAsc(petId, ownerId);
    }
    
    public List<PetGallery> getGalleryByPet(Long petId) {
        return galleryRepository.findByPetIdOrderByDisplayOrderAsc(petId);
    }
    
    public List<PetGallery> getGalleryByOwner(Long ownerId) {
        return galleryRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }
    
    public PetGallery updateGalleryImage(Long imageId, PetGalleryRequest request, Long ownerId) {
        PetGallery gallery = galleryRepository.findById(imageId)
                .filter(g -> g.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Gallery image not found"));
        
        gallery.setImageUrl(request.getImageUrl());
        gallery.setCaption(request.getCaption());
        gallery.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : gallery.getDisplayOrder());
        
        return galleryRepository.save(gallery);
    }
    
    public boolean deleteGalleryImage(Long imageId, Long ownerId) {
        Optional<PetGallery> gallery = galleryRepository.findById(imageId)
                .filter(g -> g.getOwnerId().equals(ownerId));
        
        if (gallery.isPresent()) {
            // Delete physical file
            deletePhysicalFile(gallery.get().getImageUrl());
            
            // Delete database record
            galleryRepository.delete(gallery.get());
            return true;
        }
        return false;
    }
    
    public void deleteAllGalleryImagesByPet(Long petId, Long ownerId) {
        List<PetGallery> images = galleryRepository.findByPetIdAndOwnerIdOrderByDisplayOrderAsc(petId, ownerId);
        
        // Delete physical files
        for (PetGallery image : images) {
            deletePhysicalFile(image.getImageUrl());
        }
        
        // Delete database records
        galleryRepository.deleteByPetIdAndOwnerId(petId, ownerId);
    }
    
    public void deleteAllGalleryImagesByPet(Long petId) {
        List<PetGallery> images = galleryRepository.findByPetIdOrderByDisplayOrderAsc(petId);
        
        // Delete physical files
        for (PetGallery image : images) {
            deletePhysicalFile(image.getImageUrl());
        }
        
        // Delete database records
        galleryRepository.deleteByPetId(petId);
    }
    
    public PetGallery updateDisplayOrder(Long imageId, Integer newOrder, Long ownerId) {
        PetGallery gallery = galleryRepository.findById(imageId)
                .filter(g -> g.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Gallery image not found"));
        
        gallery.setDisplayOrder(newOrder);
        return galleryRepository.save(gallery);
    }
    
    private void deletePhysicalFile(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.contains("/api/pet-owner/gallery/images/")) {
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(UPLOAD_DIR + filename);
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }
    }
    
    public Long getImageCountByPet(Long petId, Long ownerId) {
        return galleryRepository.countByPetIdAndOwnerId(petId, ownerId);
    }
}
