package TechWiz.admin.models;

public enum PostCategory {
    CARE_TIPS("Care Tips"),
    HEALTH_ADVICE("Health Advice"),
    TRAINING("Training"),
    NUTRITION("Nutrition"),
    GROOMING("Grooming"),
    BEHAVIOR("Behavior"),
    NEWS("News"),
    GENERAL("General");
    
    private final String displayName;
    
    PostCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
