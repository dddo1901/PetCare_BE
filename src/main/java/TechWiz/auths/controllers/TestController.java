package TechWiz.auths.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.models.dto.ApiResponse;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public ApiResponse healthCheck() {
        return ApiResponse.success("PetCare Backend is running successfully!");
    }

    @GetMapping("/email")
    public ApiResponse emailTest() {
        return ApiResponse.success("Email configuration: nthieu18@gmail.com");
    }

    @GetMapping("/roles")
    public ApiResponse getRoles() {
        String[] roles = {"PET_OWNER", "VETERINARIAN", "SHELTER", "ADMIN"};
        return ApiResponse.success("Available roles", roles);
    }
}
