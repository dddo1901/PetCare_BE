package TechWiz.auths.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded pet photos
        registry.addResourceHandler("/api/pet-owner/photos/**")
                .addResourceLocations("file:uploads/pets/")
                .setCachePeriod(3600); // Cache for 1 hour
        
        // Serve other static files if needed
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);
    }
}
