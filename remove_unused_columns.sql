-- Migration script to remove unused columns from products table
-- Run this script manually in your MySQL database

-- First, check current table structure
-- DESCRIBE products;

-- Remove unused columns that were causing constraint violations
ALTER TABLE products 
DROP COLUMN IF EXISTS stock_quantity,
DROP COLUMN IF EXISTS target_pet_type,
DROP COLUMN IF EXISTS additional_images,
DROP COLUMN IF EXISTS weight_kg,
DROP COLUMN IF EXISTS dimensions_cm,
DROP COLUMN IF EXISTS age_group,
DROP COLUMN IF EXISTS special_features,
DROP COLUMN IF EXISTS specifications,
DROP COLUMN IF EXISTS ingredients,
DROP COLUMN IF EXISTS usage_instructions;

-- Verify the changes
-- DESCRIBE products;