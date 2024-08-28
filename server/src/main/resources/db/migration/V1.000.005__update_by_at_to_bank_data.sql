ALTER TABLE bank_data
    ADD COLUMN
        updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE bank_data
    ADD COLUMN updated_by character varying(128) DEFAULT NULL;