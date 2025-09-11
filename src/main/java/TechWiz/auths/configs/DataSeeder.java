package TechWiz.auths.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import TechWiz.auths.models.Role;
import TechWiz.auths.models.User;
import TechWiz.auths.repositories.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (!userRepository.existsByEmail("admin@petcare.com")) {
            User admin = new User();
            admin.setEmail("admin@petcare.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("PetCare Admin");
            admin.setPhoneNumber("1234567890");
            admin.setRole(Role.ADMIN);
            admin.setIsActive(true);
            admin.setIsEmailVerified(true);
            
            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
            System.out.println("Email: admin@petcare.com");
            System.out.println("Password: admin123");
        }
    }
}
