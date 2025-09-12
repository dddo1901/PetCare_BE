package TechWiz.admin.controllers;

import TechWiz.auths.configs.DatabaseUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/database")
public class DatabaseMaintenanceController {

    @Autowired
    private DatabaseUtility databaseUtility;

    /**
     * Get status of vet_* tables
     */
    @GetMapping("/vet-tables/status")
    public ResponseEntity<Map<String, Object>> getVetTablesStatus() {
        try {
            List<String> vetTables = databaseUtility.getTablesWithPrefix("vet_");
            Map<String, Object> response = new HashMap<>();
            Map<String, Integer> tableStats = new HashMap<>();
            
            for (String table : vetTables) {
                int rowCount = databaseUtility.getTableRowCount(table);
                tableStats.put(table, rowCount);
            }
            
            response.put("tables", tableStats);
            response.put("totalTables", vetTables.size());
            response.put("message", "Retrieved vet tables status");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get table status");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Force cleanup vet tables
     */
    @DeleteMapping("/vet-tables/cleanup")
    public ResponseEntity<Map<String, String>> cleanupVetTables() {
        try {
            databaseUtility.cleanupElementCollectionTables();
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully cleaned up vet tables");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to cleanup tables");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "failed");
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Force delete data from specific table
     */
    @DeleteMapping("/table/{tableName}/data")
    public ResponseEntity<Map<String, String>> forceDeleteTableData(@PathVariable String tableName) {
        try {
            if (!databaseUtility.tableExists(tableName)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Table does not exist");
                errorResponse.put("tableName", tableName);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            int rowCountBefore = databaseUtility.getTableRowCount(tableName);
            databaseUtility.forceDeleteTableData(tableName);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully deleted data from " + tableName);
            response.put("deletedRows", String.valueOf(rowCountBefore));
            response.put("tableName", tableName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete table data");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("tableName", tableName);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Check if table exists
     */
    @GetMapping("/table/{tableName}/exists")
    public ResponseEntity<Map<String, Object>> checkTableExists(@PathVariable String tableName) {
        try {
            boolean exists = databaseUtility.tableExists(tableName);
            int rowCount = exists ? databaseUtility.getTableRowCount(tableName) : 0;
            
            Map<String, Object> response = new HashMap<>();
            response.put("tableName", tableName);
            response.put("exists", exists);
            response.put("rowCount", rowCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to check table");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("tableName", tableName);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Force drop table
     */
    @DeleteMapping("/table/{tableName}")
    public ResponseEntity<Map<String, String>> forceDropTable(@PathVariable String tableName) {
        try {
            if (!databaseUtility.tableExists(tableName)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Table does not exist");
                errorResponse.put("tableName", tableName);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            databaseUtility.forceDropTable(tableName);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully dropped table " + tableName);
            response.put("tableName", tableName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to drop table");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("tableName", tableName);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
