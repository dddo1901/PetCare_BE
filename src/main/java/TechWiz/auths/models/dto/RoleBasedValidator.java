package TechWiz.auths.models.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import TechWiz.auths.models.Role;

public class RoleBasedValidator implements ConstraintValidator<ValidRoleBasedData, RegisterRequest> {

    @Override
    public void initialize(ValidRoleBasedData constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request.getRole() == null) {
            return false;
        }

        context.disableDefaultConstraintViolation();
        boolean isValid = true;

        switch (request.getRole()) {
            case PET_OWNER:
                isValid = validatePetOwner(request, context);
                break;
            case VETERINARIAN:
                isValid = validateVeterinarian(request, context);
                break;
            case SHELTER:
                isValid = validateShelter(request, context);
                break;
            case ADMIN:
                // Admin doesn't need additional validation
                break;
        }

        return isValid;
    }

    private boolean validatePetOwner(RegisterRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;

        // Address is required for all roles (already validated at field level)
        
        // Emergency contact validation (optional but if provided, both name and phone should be present)
        if (request.getEmergencyContactName() != null && request.getEmergencyContactPhone() == null) {
            context.buildConstraintViolationWithTemplate("Emergency contact phone is required when emergency contact name is provided")
                   .addPropertyNode("emergencyContactPhone")
                   .addConstraintViolation();
            isValid = false;
        }
        
        if (request.getEmergencyContactPhone() != null && request.getEmergencyContactName() == null) {
            context.buildConstraintViolationWithTemplate("Emergency contact name is required when emergency contact phone is provided")
                   .addPropertyNode("emergencyContactName")
                   .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }

    private boolean validateVeterinarian(RegisterRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;

        // License number is required for veterinarians
        if (request.getLicenseNumber() == null || request.getLicenseNumber().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("License number is required for veterinarians")
                   .addPropertyNode("licenseNumber")
                   .addConstraintViolation();
            isValid = false;
        }

        // Experience years validation
        if (request.getExperienceYears() != null && request.getExperienceYears() < 0) {
            context.buildConstraintViolationWithTemplate("Experience years must be positive")
                   .addPropertyNode("experienceYears")
                   .addConstraintViolation();
            isValid = false;
        }

        // Available time validation
        if (request.getAvailableFromTime() != null && request.getAvailableToTime() != null) {
            if (request.getAvailableFromTime().isAfter(request.getAvailableToTime())) {
                context.buildConstraintViolationWithTemplate("Available from time must be before available to time")
                       .addPropertyNode("availableFromTime")
                       .addConstraintViolation();
                isValid = false;
            }
        }

        // Consultation fee validation
        if (request.getConsultationFee() != null && request.getConsultationFee() < 0) {
            context.buildConstraintViolationWithTemplate("Consultation fee must be positive")
                   .addPropertyNode("consultationFee")
                   .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }

    private boolean validateShelter(RegisterRequest request, ConstraintValidatorContext context) {
        boolean isValid = true;

        // Shelter name is required
        if (request.getShelterName() == null || request.getShelterName().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Shelter name is required for shelters")
                   .addPropertyNode("shelterName")
                   .addConstraintViolation();
            isValid = false;
        }

        // Contact person name is required
        if (request.getContactPersonName() == null || request.getContactPersonName().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("Contact person name is required for shelters")
                   .addPropertyNode("contactPersonName")
                   .addConstraintViolation();
            isValid = false;
        }

        // Capacity validation
        if (request.getCapacity() != null && request.getCapacity() < 1) {
            context.buildConstraintViolationWithTemplate("Capacity must be at least 1")
                   .addPropertyNode("capacity")
                   .addConstraintViolation();
            isValid = false;
        }

        // Current occupancy validation
        if (request.getCurrentOccupancy() != null && request.getCurrentOccupancy() < 0) {
            context.buildConstraintViolationWithTemplate("Current occupancy must be positive")
                   .addPropertyNode("currentOccupancy")
                   .addConstraintViolation();
            isValid = false;
        }

        // Current occupancy should not exceed capacity
        if (request.getCapacity() != null && request.getCurrentOccupancy() != null) {
            if (request.getCurrentOccupancy() > request.getCapacity()) {
                context.buildConstraintViolationWithTemplate("Current occupancy cannot exceed capacity")
                       .addPropertyNode("currentOccupancy")
                       .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
