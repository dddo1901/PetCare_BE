package TechWiz.petOwner.controllers;

import TechWiz.auths.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/pet-owner/photos")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PET_OWNER')")
public class PetPhotoController {

    private static final String UPLOAD_DIR = "uploads/pets/";
    private static final String BASE_URL = "/api/pet-owner/photos/";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhotos(
            @RequestParam("files") MultipartFile[] files,
            Authentication authentication) {
        try {
            System.out.println("=== UPLOAD PHOTOS REQUEST RECEIVED ===");
            User user = (User) authentication.getPrincipal();
            System.out.println("User: " + user.getEmail() + " (ID: " + user.getId() + ")");
            System.out.println("Files count: " + files.length);

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            List<String> uploadedUrls = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    // Check if file is empty
                    if (file.isEmpty()) {
                        errors.add("File is empty: " + file.getOriginalFilename());
                        continue;
                    }

                    // Check file type
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        errors.add("File is not an image: " + file.getOriginalFilename());
                        continue;
                    }

                    // Generate unique filename
                    String originalFilename = file.getOriginalFilename();
                    String extension = "";
                    int lastDotIndex = originalFilename.lastIndexOf(".");
                    if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
                        extension = originalFilename.substring(lastDotIndex);
                    } else {
                        extension = ".jpg"; // Default extension if no extension found
                    }
                    String uniqueFilename = UUID.randomUUID().toString() + extension;

                    // Save file
                    Path filePath = uploadPath.resolve(uniqueFilename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Generate URL
                    String fileUrl = BASE_URL + uniqueFilename;
                    uploadedUrls.add(fileUrl);

                    System.out.println("File uploaded successfully: " + uniqueFilename);
                } catch (IOException e) {
                    System.err.println("Error uploading file " + file.getOriginalFilename() + ": " + e.getMessage());
                    errors.add("Error uploading file " + file.getOriginalFilename() + ": " + e.getMessage());
                }
            }

            if (uploadedUrls.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No files were uploaded successfully",
                    "errors", errors
                ));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Photos uploaded successfully",
                "data", uploadedUrls,
                "errors", errors
            ));

        } catch (Exception e) {
            System.out.println("=== ERROR IN UPLOAD PHOTOS ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to upload photos: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{filename}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> servePhoto(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .body(fileBytes);

        } catch (IOException e) {
            System.err.println("Error serving photo " + filename + ": " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error serving photo: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deletePhoto(@PathVariable String filename, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            System.out.println("Delete photo request from user: " + user.getEmail());
            System.out.println("Filename: " + filename);

            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Files.delete(filePath);
            System.out.println("Photo deleted successfully: " + filename);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Photo deleted successfully"
            ));

        } catch (IOException e) {
            System.err.println("Error deleting photo " + filename + ": " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error deleting photo: " + e.getMessage()
            ));
        }
    }
}
