# DATABASE CLEANUP GUIDE - MYSQL READONLY ISSUE

## Vấn đề hiện tại
- Không thể xóa dữ liệu từ bảng `vet_specializations` và `vet_available_days`
- MySQL hiển thị trạng thái "readonly" cho các bảng này
- Các bảng này được tạo bởi `@ElementCollection` trong Hibernate

## Nguyên nhân
1. **@ElementCollection Mapping**: Hibernate tự động tạo các bảng phụ cho collections
2. **Foreign Key Constraints**: MySQL không cho phép xóa dữ liệu khi có ràng buộc khóa ngoại
3. **Hibernate ddl-auto**: Không xử lý tốt việc cleanup các bảng collection cũ

## Giải pháp được triển khai

### 1. DatabaseMigration Class (Tự động)
```java
// File: src/main/java/TechWiz/auths/configs/DatabaseMigration.java
// Chạy tự động khi khởi động ứng dụng
```

### 2. DatabaseUtility Class (Tiện ích)
```java
// File: src/main/java/TechWiz/auths/configs/DatabaseUtility.java
// Các phương thức xử lý database
```

### 3. DatabaseMaintenanceController (API Endpoints)
```java
// File: src/main/java/TechWiz/admin/controllers/DatabaseMaintenanceController.java
// REST API để quản lý database
```

### 4. Manual SQL Script (Thủ công)
```sql
-- File: cleanup_vet_tables.sql
-- Chạy trực tiếp trong MySQL Workbench
```

## Cách sử dụng

### Phương pháp 1: Tự động (Khuyến nghị)
1. Khởi động lại ứng dụng Spring Boot
2. DatabaseMigration sẽ tự động chạy và cleanup các bảng cũ

### Phương pháp 2: API Endpoints
1. **Kiểm tra trạng thái bảng:**
```
GET /api/admin/database/vet-tables/status
```

2. **Cleanup tất cả bảng vet:**
```
DELETE /api/admin/database/vet-tables/cleanup
```

3. **Xóa dữ liệu bảng cụ thể:**
```
DELETE /api/admin/database/table/{tableName}/data
```

4. **Kiểm tra bảng tồn tại:**
```
GET /api/admin/database/table/{tableName}/exists
```

5. **Xóa bảng hoàn toàn:**
```
DELETE /api/admin/database/table/{tableName}
```

### Phương pháp 3: SQL Script thủ công
1. Mở MySQL Workbench
2. Mở file `cleanup_vet_tables.sql`
3. Chạy toàn bộ script

### Phương pháp 4: Terminal MySQL
```bash
# Kết nối MySQL
mysql -u root -p

# Chọn database
USE your_database_name;

# Tắt foreign key check
SET FOREIGN_KEY_CHECKS = 0;

# Xóa dữ liệu
DELETE FROM vet_specializations;
DELETE FROM vet_available_days;

# Xóa bảng
DROP TABLE IF EXISTS vet_specializations;
DROP TABLE IF EXISTS vet_available_days;

# Bật lại foreign key check
SET FOREIGN_KEY_CHECKS = 1;
```

## Postman Test Collection
```json
{
    "info": {
        "name": "Database Maintenance API",
        "description": "API để quản lý và cleanup database"
    },
    "item": [
        {
            "name": "Check Vet Tables Status",
            "request": {
                "method": "GET",
                "header": [],
                "url": "{{base_url}}/api/admin/database/vet-tables/status"
            }
        },
        {
            "name": "Cleanup Vet Tables",
            "request": {
                "method": "DELETE",
                "header": [],
                "url": "{{base_url}}/api/admin/database/vet-tables/cleanup"
            }
        },
        {
            "name": "Check Table Exists",
            "request": {
                "method": "GET",
                "header": [],
                "url": "{{base_url}}/api/admin/database/table/vet_specializations/exists"
            }
        },
        {
            "name": "Force Delete Table Data",
            "request": {
                "method": "DELETE",
                "header": [],
                "url": "{{base_url}}/api/admin/database/table/vet_specializations/data"
            }
        },
        {
            "name": "Force Drop Table",
            "request": {
                "method": "DELETE",
                "header": [],
                "url": "{{base_url}}/api/admin/database/table/vet_specializations"
            }
        }
    ]
}
```

## Kiểm tra kết quả
1. **Trong MySQL Workbench:**
```sql
-- Kiểm tra các bảng còn tồn tại
SHOW TABLES LIKE 'vet_%';

-- Kiểm tra dữ liệu trong bảng
SELECT COUNT(*) FROM vet_specializations; -- Nếu bảng còn tồn tại
```

2. **Trong application logs:**
```
Starting cleanup of @ElementCollection tables...
Found table: vet_specializations with X rows
Successfully deleted X rows from vet_specializations
Successfully dropped table: vet_specializations
Cleanup completed successfully
```

## Lưu ý quan trọng
1. **Backup dữ liệu** trước khi chạy cleanup
2. **Dừng application** nếu chạy manual SQL script
3. **Test thoroughly** sau khi cleanup
4. **Monitor logs** để đảm bảo không có lỗi

## Troubleshooting
### Nếu vẫn gặp lỗi "readonly":
1. Kiểm tra user permission trong MySQL
2. Đảm bảo `FOREIGN_KEY_CHECKS = 0`
3. Chạy từng command riêng biệt
4. Sử dụng user có quyền DROP TABLE

### Nếu application không khởi động được:
1. Kiểm tra connection string database
2. Kiểm tra MySQL service đang chạy
3. Xem logs để identify lỗi cụ thể

## Contact Support
Nếu vẫn gặp vấn đề, vui lòng cung cấp:
1. Error logs từ application
2. MySQL error messages
3. Database schema hiện tại
4. Phương pháp đã thử
