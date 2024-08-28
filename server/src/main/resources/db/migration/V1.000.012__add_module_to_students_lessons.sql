-- Step 1: Add the modul_id column to students_lessons table
ALTER TABLE students_lessons
    ADD COLUMN modul_id BIGINT DEFAULT NULL;

-- Step 2: Add the foreign key constraint for modul_id
ALTER TABLE students_lessons
    ADD CONSTRAINT fk_modul
        FOREIGN KEY (modul_id)
            REFERENCES moduls(id);
