-- Script to manually drop the ElementCollection tables
-- Run this script in your MySQL database

-- Drop the vet_specializations table
DROP TABLE IF EXISTS vet_specializations;

-- Drop the vet_available_days table  
DROP TABLE IF EXISTS vet_available_days;

-- Check if tables are dropped successfully
SHOW TABLES LIKE 'vet_%';
