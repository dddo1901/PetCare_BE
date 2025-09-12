-- Script để xóa dữ liệu và bảng vet_specializations và vet_available_days
-- Chạy script này trong MySQL Workbench hoặc command line

-- 1. Tắt foreign key check tạm thời
SET FOREIGN_KEY_CHECKS = 0;

-- 2. Xóa dữ liệu từ các bảng con trước
DELETE FROM vet_specializations;
DELETE FROM vet_available_days;

-- 3. Reset auto increment nếu cần (optional)
-- ALTER TABLE vet_specializations AUTO_INCREMENT = 1;
-- ALTER TABLE vet_available_days AUTO_INCREMENT = 1;

-- 4. Drop các bảng nếu muốn xóa hoàn toàn
DROP TABLE IF EXISTS vet_specializations;
DROP TABLE IF EXISTS vet_available_days;

-- 5. Bật lại foreign key check
SET FOREIGN_KEY_CHECKS = 1;

-- 6. Kiểm tra các bảng còn lại
SHOW TABLES LIKE 'vet_%';

-- 7. Kiểm tra structure của bảng veterinarian_profiles
DESCRIBE veterinarian_profiles;
