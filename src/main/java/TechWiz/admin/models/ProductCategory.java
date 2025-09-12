package TechWiz.admin.models;

public enum ProductCategory {
    FOOD("Food & Treats"),
    TOYS("Toys & Entertainment"),
    HEALTH_CARE("Health Care & Medicine"),
    GROOMING("Grooming & Hygiene"),
    ACCESSORIES("Accessories & Clothing"),
    BEDDING("Bedding & Furniture"),
    TRAINING("Training & Behavior"),
    TRAVEL("Travel & Transport"),
    FEEDING("Feeding Supplies"),
    CLEANING("Cleaning Supplies");
    
    private final String displayName;
    
    ProductCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
