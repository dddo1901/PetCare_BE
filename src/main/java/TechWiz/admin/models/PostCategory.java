package TechWiz.admin.models;

public enum PostCategory {
    NEWS("News"),
    HEALTH_CARE("Health Care"),
    NUTRITION("Nutrition"),
    TRAINING("Training"),
    GENERAL_INFO("General Information"),
    EMERGENCY("Emergency"),
    ADOPTION("Adoption");
    
    private final String displayName;
    
    PostCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
