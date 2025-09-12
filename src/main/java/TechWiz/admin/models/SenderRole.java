package TechWiz.admin.models;

public enum SenderRole {
    ADMIN("Admin"),
    PET_OWNER("Pet Owner"), 
    VETERINARIAN("Veterinarian"),
    SHELTER("Shelter");
    
    private final String displayName;
    
    SenderRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
