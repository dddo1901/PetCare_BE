package TechWiz.admin.models;

public enum PetType {
    DOG("Dog"),
    CAT("Cat"),
    BIRD("Bird"),
    FISH("Fish"),
    RABBIT("Rabbit"),
    HAMSTER("Hamster"),
    REPTILE("Reptile"),
    ALL_PETS("All Pets");
    
    private final String displayName;
    
    PetType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
