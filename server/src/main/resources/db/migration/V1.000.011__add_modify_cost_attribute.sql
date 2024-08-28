ALTER TABLE contracts
    DROP COLUMN rate;

ALTER TABLE  teachers RENAME  hourly_rate TO single_lesson_cost;
ALTER TABLE  teachers RENAME  hourly_rate_group TO group_lesson_cost;

ALTER TABLE moduls ADD COLUMN single_lesson_cost numeric(10,2) DEFAULT 0.00 NOT NULL;

ALTER TABLE moduls ADD COLUMN group_lesson_cost numeric(10,2) DEFAULT 0.00 NOT NULL;

ALTER TABLE moduls ADD COLUMN single_lesson_allowed boolean DEFAULT  true NOT NULL;
ALTER TABLE moduls ADD COLUMN group_lesson_allowed boolean DEFAULT  true NOT NULL;