-- Step 1: Add the modul_id column to students_lessons table
ALTER TABLE contracts
    ADD COLUMN reference_contract_number VARCHAR(40) DEFAULT NULL;

