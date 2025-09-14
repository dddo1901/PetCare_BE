package TechWiz.common.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class ImageUploadController {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.upload.max-file-size:10MB}")
    private String maxFileSize;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    private static final String[] ALLOWED_EXTENSIONS = {
        "jpg", "jpeg", "png", "gif", "webp", "bmp"
    };
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate file
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "No file selected");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check file size
            if (file.getSize() > MAX_FILE_SIZE) {
                response.put("success", false);
                response.put("message", "File size exceeds maximum limit of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate file type
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                response.put("success", false);
                response.put("message", "Invalid filename");
                return ResponseEntity.badRequest().body(response);
            }
            
            String extension = getFileExtension(originalFilename).toLowerCase();
            if (!isAllowedExtension(extension)) {
                response.put("success", false);
                response.put("message", "File type not allowed. Only images are permitted.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Generate unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueId = UUID.randomUUID().toString().substring(0, 8);
            String filename = String.format("%s_%s_%s.%s", 
                folder, timestamp, uniqueId, extension);
            
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, folder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Generate URL
            String fileUrl = String.format("%s%s/uploads/%s/%s", 
                baseUrl, contextPath, folder, filename);
            
            // Prepare response
            response.put("success", true);
            response.put("message", "File uploaded successfully");
            response.put("filename", filename);
            response.put("originalName", originalFilename);
            response.put("url", fileUrl);
            response.put("size", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("uploadedAt", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/images/multiple")
    public ResponseEntity<?> uploadMultipleImages(
            @RequestParam("images") MultipartFile[] files,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (files.length == 0) {
                response.put("success", false);
                response.put("message", "No files selected");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (files.length > 10) {
                response.put("success", false);
                response.put("message", "Maximum 10 files allowed per upload");
                return ResponseEntity.badRequest().body(response);
            }
            
            Map<String, Object>[] uploadResults = new Map[files.length];
            boolean allSuccess = true;
            
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                Map<String, Object> fileResult = new HashMap<>();
                
                try {
                    // Validate and upload each file
                    if (file.isEmpty()) {
                        fileResult.put("success", false);
                        fileResult.put("message", "Empty file");
                        allSuccess = false;
                    } else {
                        ResponseEntity<?> uploadResponse = uploadImage(file, folder);
                        if (uploadResponse.getStatusCode() == HttpStatus.OK) {
                            fileResult = (Map<String, Object>) uploadResponse.getBody();
                        } else {
                            fileResult.put("success", false);
                            fileResult.put("message", "Upload failed");
                            allSuccess = false;
                        }
                    }
                } catch (Exception e) {
                    fileResult.put("success", false);
                    fileResult.put("message", "Error: " + e.getMessage());
                    allSuccess = false;
                }
                
                fileResult.put("originalName", file.getOriginalFilename());
                uploadResults[i] = fileResult;
            }
            
            response.put("success", allSuccess);
            response.put("message", allSuccess ? "All files uploaded successfully" : "Some files failed to upload");
            response.put("results", uploadResults);
            response.put("totalFiles", files.length);
            response.put("uploadedAt", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to upload files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/image/{folder}/{filename}")
    public ResponseEntity<?> deleteImage(
            @PathVariable String folder,
            @PathVariable String filename) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate filename to prevent directory traversal attacks
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                response.put("success", false);
                response.put("message", "Invalid filename");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if file exists
            Path filePath = Paths.get(uploadDir, folder, filename);
            if (!Files.exists(filePath)) {
                response.put("success", false);
                response.put("message", "File not found");
                return ResponseEntity.notFound().build();
            }
            
            // Delete file
            Files.delete(filePath);
            
            response.put("success", true);
            response.put("message", "File deleted successfully");
            response.put("filename", filename);
            response.put("deletedAt", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to delete file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}