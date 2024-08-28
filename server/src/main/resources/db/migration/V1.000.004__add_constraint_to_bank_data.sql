ALTER TABLE bank_data
    ADD CONSTRAINT unique_user_id_bank_data UNIQUE (user_id);