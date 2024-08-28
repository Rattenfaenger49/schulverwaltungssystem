CREATE SCHEMA IF NOT EXISTS public;
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;
SET search_path TO public;

CREATE TABLE addresses (
                           id BIGSERIAL PRIMARY KEY,
                           street character varying(127),
                           street_number character varying(127),
                           city character varying(127),
                           state character varying(127),
                           country character varying(127),
                           postal integer,
                           updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                           updated_by character varying(128)
);
CREATE TABLE contracts (
                           id BIGSERIAL PRIMARY KEY,
                           start_at date,
                           end_at date,
                           status character varying(64) DEFAULT 'UNKNOWN',
                           institution_id bigint,
                           student_id bigint NOT NULL,
                           contact_id bigint,
                           contract_number character varying(64),
                           comment character varying(64),
                           contract_type character varying(64) NOT NULL,
                           updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                           updated_by character varying(128)
);
CREATE TABLE moduls (
                        id BIGSERIAL PRIMARY KEY,
                        modul_type character varying(64) NOT NULL,
                        units double precision,
                        contract_id bigint,
                        updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                        updated_by character varying(128),
                        lesson_duration character varying(32) NOT NULL
);
CREATE TABLE lessons (
                         id BIGSERIAL PRIMARY KEY,
                         start_at timestamp with time zone NOT NULL,
                         units double precision,
                         description character varying(512),
                         teacher_id bigint,
                         modul_type character varying(64) NOT NULL,
                         lesson_type character varying(64) NOT NULL,
                         contract_type character varying(64) NOT NULL,
                         signature_id bigint,
                         updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                         updated_by character varying(128) DEFAULT NULL::character varying
);
CREATE TABLE students_lessons (
                                 student_id bigint,
                                 lesson_id bigint
);
CREATE TABLE students (
                          id BIGSERIAL PRIMARY KEY,
                          level character varying(16),
                          portal_access boolean DEFAULT false NOT NULL,
                          parent_id bigint
);
CREATE TABLE teachers_students (
                                 teacher_id bigint NOT NULL,
                                 student_id bigint NOT NULL
);
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       gender character varying(16) NOT NULL,
                       first_name character varying(128) NOT NULL,
                       last_name character varying(128) NOT NULL,
                       username character varying(128) NOT NULL,
                       password character varying(255),
                       phone_number character varying(20),
                       account_non_expired boolean DEFAULT true,
                       account_non_locked boolean DEFAULT true,
                       credentials_non_expired boolean DEFAULT true,
                       enabled boolean DEFAULT true,
                       verified boolean DEFAULT false,
                       address_id bigint,
                       has_provided_all_info boolean DEFAULT false,
                       comment character varying(512) DEFAULT NULL::character varying,
                       marked_for_deletion boolean DEFAULT false NOT NULL,
                       updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                       updated_by character varying(128)
);
CREATE TABLE teachers (
                          id BIGSERIAL PRIMARY KEY,
                          education character varying(255),
                          qualifications character varying(255),
                          hourly_rate numeric(10,2) DEFAULT 0.00 NOT NULL,
                          tax_id character varying(50) DEFAULT '0000000000'::character varying NOT NULL,
                          hourly_rate_group numeric(10,2) DEFAULT 0.00 NOT NULL
);
CREATE TABLE appointments (
                             id BIGSERIAL PRIMARY KEY,
                             title character varying(16) NOT NULL,
                             content text,
                             organizer bigint NOT NULL,
                             contract_type character varying(64) NOT NULL,
                             start_at timestamp without time zone NOT NULL,
                             end_at timestamp without time zone NOT NULL,
                             status character varying(255),
                             updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                             updated_by character varying(256)
);
CREATE TABLE appointments_participants (
                                          appointment_id bigint NOT NULL,
                                          user_id bigint NOT NULL,
                                          PRIMARY KEY (appointment_id, user_id),
                                          FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
                                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE TABLE bank_data (
                           id BIGSERIAL PRIMARY KEY,
                           bank_name character varying(255) NOT NULL,
                           bic character varying(20),
                           iban character varying(34) NOT NULL,
                           account_holder_name character varying(255) NOT NULL,
                           user_id bigint NOT NULL
);
CREATE TABLE confirmation_token (
                                    id BIGSERIAL PRIMARY KEY,
                                    token character varying(255) NOT NULL,
                                    created_at timestamp without time zone NOT NULL,
                                    expires_at timestamp without time zone NOT NULL,
                                    confirmed_at timestamp without time zone,
                                    user_id bigint NOT NULL,
                                    deleted boolean DEFAULT false NOT NULL
);
CREATE TABLE contacts (
                          id BIGSERIAL PRIMARY KEY,
                          gender character varying(16) NOT NULL,
                          first_name character varying(100) NOT NULL,
                          last_name character varying(100) NOT NULL,
                          phone_number character varying(20) NOT NULL,
                          email character varying(255) NOT NULL,
                          institution_id bigint,
                          updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                          updated_by character varying(128)
);
CREATE TABLE file_metadata (
                               id BIGSERIAL PRIMARY KEY,
                               file_name character varying(255) NOT NULL,
                               file_type character varying(255) NOT NULL,
                               entity_id bigint NOT NULL,
                               full_path character varying(255) NOT NULL,
                               size bigint NOT NULL,
                               url character varying(1024),
                               uploaded_at timestamp without time zone NOT NULL,
                               uploaded_by character varying(255) NOT NULL,
                               storage_type character varying(32) NOT NULL,
                               gcp_file_name character varying(255),
                               file_category character varying(64)NOT NULL
);

CREATE TABLE institutions (
                              id BIGSERIAL PRIMARY KEY,
                              name character varying(255),
                              email character varying(255),
                              phone_number character varying(255),
                              address_id bigint,
                              updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                              updated_by character varying(128)
);
CREATE TABLE invoices (
                          id BIGSERIAL PRIMARY KEY,
                          invoice_number character varying(256) NOT NULL,
                          teacher_id bigint,
                          invoice_date date NOT NULL,
                          subtotal numeric(10,2),
                          taxes numeric(10,2),
                          total_amount numeric(10,2),
                          invoice_status character varying(64) NOT NULL,
                          payment_date date,
                          file_id bigint NOT NULL,
                          notes character varying(512),
                          signature_id bigint,
                          updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                          updated_by character varying(256) NOT NULL
);
CREATE TABLE lessons_signatures (
                                   lesson_id bigint NOT NULL,
                                   signature_id bigint NOT NULL
);
CREATE TABLE parents (
                         id BIGSERIAL PRIMARY KEY,
                         gender character varying(16) NOT NULL,
                         first_name character varying(100) DEFAULT NULL::character varying,
                         last_name character varying(100) DEFAULT NULL::character varying,
                         email character varying(255) DEFAULT NULL::character varying,
                         phone_number character varying(20) DEFAULT NULL::character varying
);
CREATE TABLE permission_role (
                                 role_id bigint NOT NULL,
                                 permission_id bigint NOT NULL
);
CREATE TABLE permissions (
                             id BIGSERIAL PRIMARY KEY,
                             description character varying(255),
                             name character varying(255) NOT NULL,
                             requires_verification boolean DEFAULT false
);
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       description character varying(255),
                       name character varying(255) NOT NULL
);
CREATE TABLE signatures (
                            id BIGSERIAL PRIMARY KEY,
                            signature bytea,
                            signed_at timestamp(6) without time zone,
                            signed_by character varying(255)
);
CREATE TABLE supervisors (
                             id BIGSERIAL PRIMARY KEY,
                             section character varying(64)
);
CREATE TABLE tokens (
                        id BIGSERIAL PRIMARY KEY,
                        token text NOT NULL,
                        token_type smallint DEFAULT 0 NOT NULL,
                        revoked boolean DEFAULT false,
                        user_id bigint NOT NULL
);
CREATE TABLE user_role (
                           user_id bigint NOT NULL,
                           role_id bigint NOT NULL
);

ALTER TABLE ONLY lessons_signatures
    ADD CONSTRAINT lessons_signatures_pkey PRIMARY KEY (lesson_id, signature_id);
ALTER TABLE ONLY permission_role
    ADD CONSTRAINT permission_role_pkey PRIMARY KEY (role_id, permission_id);
ALTER TABLE ONLY teachers_students
    ADD CONSTRAINT teachers_students_pkey PRIMARY KEY (teacher_id, student_id);
ALTER TABLE ONLY users
    ADD CONSTRAINT uk_username UNIQUE (username);
ALTER TABLE ONLY moduls
    ADD CONSTRAINT ukk7gvy9wd94q6kaqo3ky1cnpoj UNIQUE (contract_id, modul_type);
ALTER TABLE ONLY file_metadata
    ADD CONSTRAINT unique_fullpath UNIQUE (full_path);
ALTER TABLE ONLY user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);
ALTER TABLE ONLY contracts
    ADD CONSTRAINT contracts_student_id_fkey FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY bank_data
    ADD CONSTRAINT bank_data_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY confirmation_token
    ADD CONSTRAINT confirmation_token_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY invoices
    ADD CONSTRAINT fk_file_id FOREIGN KEY (file_id) REFERENCES file_metadata(id) ON DELETE CASCADE;
ALTER TABLE ONLY appointments
    ADD CONSTRAINT fk_organizer FOREIGN KEY (organizer) REFERENCES users(id);
ALTER TABLE ONLY students
    ADD CONSTRAINT fk_parent_student FOREIGN KEY (parent_id) REFERENCES parents(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY permission_role
    ADD CONSTRAINT fk_permission_role_permissions FOREIGN KEY (permission_id) REFERENCES permissions(id);
ALTER TABLE ONLY permission_role
    ADD CONSTRAINT fk_permission_role_roles FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE ONLY tokens
    ADD CONSTRAINT fk_tokens_users FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY appointments_participants
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE ONLY user_role
    ADD CONSTRAINT fk_user_role_roles FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE ONLY user_role
    ADD CONSTRAINT fk_user_role_users FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY contracts
    ADD CONSTRAINT fkc4rwpitwmdksdm08ll7ogxqhk FOREIGN KEY (contact_id) REFERENCES contacts(id);
ALTER TABLE ONLY contracts
    ADD CONSTRAINT fkh3d4256j3dvy9npmtw7on8nlt FOREIGN KEY (institution_id) REFERENCES institutions(id);
ALTER TABLE ONLY contacts
    ADD CONSTRAINT fki1rcporff7129nnoh9ikpamwv FOREIGN KEY (institution_id) REFERENCES institutions(id);
ALTER TABLE ONLY institutions
    ADD CONSTRAINT institutions_address_id_fkey FOREIGN KEY (address_id) REFERENCES addresses(id);
ALTER TABLE ONLY invoices
    ADD CONSTRAINT invoices_signature_id_fkey FOREIGN KEY (signature_id) REFERENCES signatures(id);ALTER TABLE ONLY invoices
    ADD CONSTRAINT invoices_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES teachers(id);ALTER TABLE ONLY lessons_signatures
    ADD CONSTRAINT lessons_signatures_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES lessons(id);ALTER TABLE ONLY lessons_signatures
    ADD CONSTRAINT lessons_signatures_signature_id_fkey FOREIGN KEY (signature_id) REFERENCES signatures(id);ALTER TABLE ONLY lessons
    ADD CONSTRAINT lessons_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON UPDATE CASCADE ON DELETE SET NULL;
ALTER TABLE ONLY moduls
    ADD CONSTRAINT moduls_fk FOREIGN KEY (contract_id) REFERENCES contracts(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY students_lessons
    ADD CONSTRAINT students_lessons_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY students_lessons
    ADD CONSTRAINT students_lessons_student_id_fkey FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY students
    ADD CONSTRAINT students_id_fkey FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY supervisors
    ADD CONSTRAINT supervisors_id_fkey FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY teachers_students
    ADD CONSTRAINT teachers_students_student_id_fkey FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY teachers_students
    ADD CONSTRAINT teachers_students_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY teachers
    ADD CONSTRAINT teachers_id_fkey FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE ONLY users
    ADD CONSTRAINT users_address_id_fkey FOREIGN KEY (address_id) REFERENCES addresses(id);

CREATE INDEX file_type_file_metadata ON file_metadata USING btree (file_type);
CREATE INDEX file_category_file_metadata ON file_metadata USING btree (file_category);
CREATE INDEX idx_address_users ON users USING btree (address_id);
CREATE INDEX idx_appointment_ap ON appointments_participants USING btree (appointment_id);
CREATE INDEX idx_contact_contracts ON contracts USING btree (contact_id);
CREATE INDEX idx_contract_moduls ON moduls USING btree (contract_id);
CREATE INDEX idx_contract_type_lessons ON lessons USING btree (contract_type);
CREATE INDEX idx_entity_file_metadata ON file_metadata USING btree (entity_id);
CREATE INDEX idx_file_invoices ON invoices USING btree (file_id);
CREATE INDEX idx_full_path_file_metadata ON file_metadata USING btree (full_path);
CREATE INDEX idx_lesson_lessons_signatures ON lessons_signatures USING btree (lesson_id);
CREATE INDEX idx_modul_lessons ON lessons USING btree (modul_type);
CREATE INDEX idx_modul_moduls ON moduls USING btree (modul_type);
CREATE INDEX idx_name_roles ON roles USING btree (name);
CREATE INDEX idx_organizer_appointment ON appointments USING btree (organizer);
CREATE INDEX idx_parent_students ON students USING btree (parent_id);
CREATE INDEX idx_signature_invoices ON invoices USING btree (signature_id);
CREATE INDEX idx_signature_lessons_signatures ON lessons_signatures USING btree (signature_id);
CREATE INDEX idx_signature_lessons ON lessons USING btree (signature_id);
CREATE INDEX idx_students_contracts ON contracts USING btree (student_id);
CREATE INDEX idx_students_teachers_students ON teachers_students USING btree (student_id);
CREATE INDEX idx_teacher_invoices ON invoices USING btree (teacher_id);
CREATE INDEX idx_teacher_lessons ON lessons USING btree (teacher_id);
CREATE INDEX idx_teacher_teachers_students ON teachers_students USING btree (teacher_id);
CREATE INDEX idx_token_tokens ON tokens USING btree (token);
CREATE INDEX idx_user_ap ON appointments_participants USING btree (user_id);
CREATE INDEX idx_user_bank_data ON bank_data USING btree (user_id);
CREATE INDEX idx_user_id_tokens ON tokens USING btree (user_id);
CREATE INDEX idx_username_users ON users USING btree (username);
CREATE INDEX idx_users_fullname ON users USING btree (((((last_name)::text || ' '::text) || (first_name)::text)));
CREATE INDEX name_file_metadata ON file_metadata USING btree (file_name);
CREATE INDEX start_at_lessons ON lessons USING btree (start_at);
