package TechWiz.auths.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Database Migration Component
 * This runs after application startup to handle schema changes
 */
@Component
public class DatabaseMigration implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Disable foreign key checks temporarily
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Clear data from child tables first
            clearTableData("vet_specializations");
            clearTableData("vet_available_days");
            
            // Drop the problematic tables if they exist
            dropTableIfExists("vet_specializations");
            dropTableIfExists("vet_available_days");
            
            // Re-enable foreign key checks
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("Database migration completed successfully");
        } catch (Exception e) {
            // Make sure to re-enable foreign key checks even if there's an error
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            } catch (Exception ignored) {}
            System.err.println("Database migration failed: " + e.getMessage());
            throw e;
        }
    }

    private void dropTableIfExists(String tableName) {
        try {
            // Check if table exists
            String checkQuery = "SELECT COUNT(*) FROM information_schema.tables " +
                              "WHERE table_schema = DATABASE() AND table_name = ?";
            
            Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, tableName);
            
            if (count != null && count > 0) {
                // Table exists, drop it
                String dropQuery = "DROP TABLE " + tableName;
                jdbcTemplate.execute(dropQuery);
                System.out.println("Successfully dropped table: " + tableName);
            }
        } catch (Exception e) {
            System.err.println("Error dropping table " + tableName + ": " + e.getMessage());
        }
    }

    private void clearTableData(String tableName) {
        try {
            // Check if table exists
            String checkQuery = "SELECT COUNT(*) FROM information_schema.tables " +
                              "WHERE table_schema = DATABASE() AND table_name = ?";
            
            Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, tableName);
            
            if (count != null && count > 0) {
                // Clear all data from table
                String clearQuery = "DELETE FROM " + tableName;
                int rowsAffected = jdbcTemplate.update(clearQuery);
                System.out.println("Cleared " + rowsAffected + " rows from table: " + tableName);
            }
        } catch (Exception e) {
            System.err.println("Error clearing table " + tableName + ": " + e.getMessage());
        }
    }
}
