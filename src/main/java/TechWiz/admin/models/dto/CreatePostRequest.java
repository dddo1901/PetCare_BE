package TechWiz.admin.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import TechWiz.admin.models.PostCategory;

@Data
public class CreatePostRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Category is required")
    private PostCategory category;
    
    private String imageUrl;
    
    @NotNull(message = "Published status is required")
    private Boolean isPublished;
}
