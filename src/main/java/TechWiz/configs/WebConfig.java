package TechWiz.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded pet photos
        registry.addResourceHandler("/api/pet-owner/photos/**")
                .addResourceLocations("file:uploads/pets/");
        
        // Serve other static files if needed
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
