
INSERT INTO roles (name, description) VALUES
                                          ('role_admin', 'Role for users that carry out administrative functions on the application'),
                                          ('role_teacher', ''),
                                          ('role_student', ''),
                                          ('role_parent', ''),
                                          ('role_supervisro', '');
### TODO change to test data
INSERT INTO users (first_name,last_name,username,"password",phone_number,account_non_expired,account_non_locked,credentials_non_expired,enabled,verified,address_id,has_provided_all_info,title,last_updated_timestamp,last_updated_by,"comment") VALUES
    ('Test','Test','username@test.com','','+491629276912',true,true,true,true,true,774,true,'HERR','2024-02-01 21:05:55.710775',NULL,NULL);


