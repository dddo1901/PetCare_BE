package TechWiz.auths.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Database Utility for handling schema fixes and data cleanup
 */
@Component
public class DatabaseUtility {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Force delete data from tables that are showing as readonly in MySQL
     */
    public void forceDeleteTableData(String tableName) {
        try {
            // Disable foreign key constraints
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Delete all data
            String deleteQuery = "DELETE FROM " + tableName;
            int deletedRows = jdbcTemplate.update(deleteQuery);
            
            // Reset auto increment
            jdbcTemplate.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1");
            
            // Re-enable foreign key constraints
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("Successfully deleted " + deletedRows + " rows from " + tableName);
            
        } catch (Exception e) {
            // Always re-enable foreign key checks
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            } catch (Exception ignored) {}
            
            System.err.println("Error deleting data from " + tableName + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete data from " + tableName, e);
        }
    }

    /**
     * Get all table names with a specific prefix
     */
    public List<String> getTablesWithPrefix(String prefix) {
        String query = "SELECT table_name FROM information_schema.tables " +
                      "WHERE table_schema = DATABASE() AND table_name LIKE ?";
        return jdbcTemplate.queryForList(query, String.class, prefix + "%");
    }

    /**
     * Check if table exists
     */
    public boolean tableExists(String tableName) {
        String query = "SELECT COUNT(*) FROM information_schema.tables " +
                      "WHERE table_schema = DATABASE() AND table_name = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, tableName);
        return count != null && count > 0;
    }

    /**
     * Get row count of a table
     */
    public int getTableRowCount(String tableName) {
        if (!tableExists(tableName)) {
            return 0;
        }
        
        try {
            String query = "SELECT COUNT(*) FROM " + tableName;
            Integer count = jdbcTemplate.queryForObject(query, Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            System.err.println("Error getting row count for " + tableName + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Force drop table (bypassing foreign key constraints)
     */
    public void forceDropTable(String tableName) {
        try {
            if (!tableExists(tableName)) {
                System.out.println("Table " + tableName + " does not exist");
                return;
            }

            // Disable foreign key constraints
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Drop table
            jdbcTemplate.execute("DROP TABLE " + tableName);
            
            // Re-enable foreign key constraints
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("Successfully dropped table: " + tableName);
            
        } catch (Exception e) {
            // Always re-enable foreign key checks
            try {
                jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            } catch (Exception ignored) {}
            
            System.err.println("Error dropping table " + tableName + ": " + e.getMessage());
            throw new RuntimeException("Failed to drop table " + tableName, e);
        }
    }

    /**
     * Clean up orphaned @ElementCollection tables
     */
    public void cleanupElementCollectionTables() {
        try {
            System.out.println("Starting cleanup of @ElementCollection tables...");
            
            // Get all vet_* tables
            List<String> vetTables = getTablesWithPrefix("vet_");
            
            for (String table : vetTables) {
                if (table.equals("veterinarian_profiles")) {
                    continue; // Skip main table
                }
                
                int rowCount = getTableRowCount(table);
                System.out.println("Found table: " + table + " with " + rowCount + " rows");
                
                // Force delete data and drop table
                if (rowCount > 0) {
                    forceDeleteTableData(table);
                }
                forceDropTable(table);
            }
            
            System.out.println("Cleanup completed successfully");
            
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            throw new RuntimeException("Cleanup failed", e);
        }
    }
}
