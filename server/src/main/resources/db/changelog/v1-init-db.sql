
CREATE TABLE addresses (
                           id bigint NOT NULL,
                           street character varying(127),
                           hausnummer character varying(127),
                           city character varying(127),
                           state character varying(127),
                           country character varying(127),
                           postal integer,
                           last_updated_timestamp timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                           last_updated_by character varying(128)
);







CREATE TABLE contracts (
                           id bigint NOT NULL,
                           start_at date,
                           end_at date,
                           status smallint DEFAULT 1 NOT NULL,
                           institution_id bigint,
                           student_id bigint NOT NULL,
                           contact_id bigint,
                           contract_number character varying(64),
                           last_updated_timestamp timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                           last_updated_by character varying(128),
                           comment character varying(512),
                           contract_type smallint,
                           CONSTRAINT contracts_contract_type_check CHECK (((contract_type >= 0) AND (contract_type <= 2)))
);



CREATE TABLE lessons (
                         id bigint NOT NULL,
                         start_at timestamp with time zone NOT NULL,
                         units double precision,
                         description character varying(512),
                         teacher_id bigint,
                         modul smallint DEFAULT 11 NOT NULL,
                         lesson_type smallint DEFAULT 3 NOT NULL,
                         last_updated_timestamp timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                         last_updated_by character varying(128) DEFAULT NULL::character varying,
                         signature bytea,
                         contract_type smallint,
                         CONSTRAINT lessons_contract_type_check CHECK (((contract_type >= 0) AND (contract_type <= 2)))
);



CREATE TABLE moduls (
                        id bigint NOT NULL,
                        modul smallint DEFAULT 11 NOT NULL,
                        units double precision,
                        contract_id bigint,
                        last_updated_timestamp timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                        last_updated_by character varying(128)
);



CREATE TABLE students (
                          id bigint NOT NULL,
                          level character varying(255),
                          portal_access boolean DEFAULT false NOT NULL,
                          parent_id bigint
);



CREATE TABLE teacher_student (
                                 teacher_id bigint NOT NULL,
                                 student_id bigint NOT NULL
);



CREATE TABLE teachers (
                          id bigint NOT NULL,
                          education character varying(255),
                          qualifications character varying(255)
);




CREATE TABLE users (
                       id bigint NOT NULL,
                       first_name character varying(255) NOT NULL,
                       last_name character varying(255) NOT NULL,
                       username character varying(255) NOT NULL,
                       password character varying(255),
                       phone_number character varying(255),
                       account_non_expired boolean DEFAULT true,
                       account_non_locked boolean DEFAULT true,
                       credentials_non_expired boolean DEFAULT true,
                       enabled boolean DEFAULT true,
                       verified boolean DEFAULT false,
                       address_id bigint,
                       has_provided_all_info boolean DEFAULT false,
                       title smallint DEFAULT 0,
                       last_updated_timestamp timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                       last_updated_by character varying(128),
                       comment character varying(512) DEFAULT NULL::character varying,
                       marked_for_deletion boolean DEFAULT false NOT NULL
);





CREATE TABLE confirmation_token (
                                    id bigint NOT NULL,
                                    token character varying(255) NOT NULL,
                                    created_at timestamp without time zone NOT NULL,
                                    expires_at timestamp without time zone NOT NULL,
                                    confirmed_at timestamp without time zone,
                                    user_id bigint NOT NULL,
                                    deleted boolean DEFAULT false NOT NULL
);







CREATE TABLE contacts (
                          id bigint NOT NULL,
                          title smallint DEFAULT 2 NOT NULL,
                          first_name character varying(100) NOT NULL,
                          last_name character varying(100) NOT NULL,
                          phone_number character varying(20) NOT NULL,
                          email character varying(100) NOT NULL,
                          last_updated_timestamp timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                          last_updated_by character varying(128),
                          institution_id bigint
);



CREATE TABLE files (
                       id bigint NOT NULL,
                       name character varying(255) NOT NULL,
                       type character varying(50),
                       api_path character varying(255),
                       full_path character varying(255),
                       size bigint,
                       url character varying(1024),
                       upload_date timestamp without time zone,
                       uploaded_by character varying(100),
                       storage_type smallint,
                       gcp_file_name character varying(255),
                       path character varying(255)
);




CREATE TABLE institutions (
                              id bigint NOT NULL,
                              name character varying(255),
                              email character varying(255),
                              phone_number character varying(255),
                              address_id bigint,
                              title character varying(15),
                              last_updated_timestamp timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
                              last_updated_by character varying(128)
);



CREATE TABLE invoices (
                          id bigint NOT NULL,
                          invoice_number bigint NOT NULL,
                          bgnumber character varying(20),
                          total_invoice_amount double precision,
                          date timestamp with time zone NOT NULL,
                          student_id bigint NOT NULL
);







CREATE TABLE parents (
                         id bigint NOT NULL,
                         title smallint DEFAULT 2 NOT NULL,
                         first_name character varying(100) NOT NULL,
                         last_name character varying(100) NOT NULL,
                         email character varying(100) NOT NULL,
                         phone_number character varying(20) NOT NULL
);


CREATE TABLE permission_role (
                                 role_id bigint NOT NULL,
                                 permission_id bigint NOT NULL
);



CREATE TABLE permissions (
                             id bigint NOT NULL,
                             description character varying(255),
                             name character varying(255) NOT NULL,
                             requires_verification boolean DEFAULT false
);






CREATE TABLE roles (
                       id bigint NOT NULL,
                       description character varying(255),
                       name character varying(255) NOT NULL
);




CREATE TABLE signatures (
                            id bigint NOT NULL,
                            signature bytea,
                            signed_at timestamp(6) without time zone,
                            signed_by character varying(255),
                            lesson_id bigint
);



CREATE TABLE student_lessons (
                                 student_id bigint,
                                 lesson_id bigint
);





CREATE TABLE supervisors (
                             id bigint NOT NULL,
                             section character varying(64)
);




CREATE TABLE tokens (
                        id bigint NOT NULL,
                        token text NOT NULL,
                        token_type smallint DEFAULT 0 NOT NULL,
                        revoked boolean DEFAULT false,
                        user_id bigint NOT NULL
);



CREATE TABLE user_role (
                           user_id bigint NOT NULL,
                           role_id bigint NOT NULL
);







INSERT INTO addresses VALUES (1, 'Straße', '1a', 'Stadt', 'NRW', 'DE', 11111, '2024-01-01 00:00:00', '');

INSERT INTO users VALUES (1, 'Admin', 'Admin', 'admin@example.de', '', '+49 (xxx) xxx-xxxx', true, true, true, true, false, 1, true, 0, '2024-01-01 00:00:00', NULL,
                          'Für alle Apps wird ein initiales Administrator-Konto erstellt. Der Admin Account ist gleichzeitig ein Lehrer!', false);
INSERT INTO teachers VALUES (1, '', '');





INSERT INTO permission_role VALUES (1, 1);
INSERT INTO permission_role VALUES (1, 2);
INSERT INTO permission_role VALUES (2, 4);
INSERT INTO permission_role VALUES (2, 3);
INSERT INTO permission_role VALUES (3, 5);
INSERT INTO permission_role VALUES (4, 5);
INSERT INTO permission_role VALUES (5, 1);



INSERT INTO permissions VALUES (1, 'Permission to fetch all users', 'users:read', true);
INSERT INTO permissions VALUES (2, 'Permission to fetch all users', 'users:write', true);
INSERT INTO permissions VALUES (3, 'Permission to fetch all users', 'teachers:write', true);
INSERT INTO permissions VALUES (4, 'Permission to fetch all users', 'teachers:read', true);
INSERT INTO permissions VALUES (5, 'Permission to fetch all users', 'students:read', true);
INSERT INTO permissions VALUES (6, 'Permission to fetch all users', 'students:write', true);


INSERT INTO roles VALUES (1, 'Role for users that carry out administrative functions on the application', 'role_admin');
INSERT INTO roles VALUES (2, '', 'role_teacher');
INSERT INTO roles VALUES (3, '', 'role_student');
INSERT INTO roles VALUES (4, '', 'role_parent');
INSERT INTO roles VALUES (5, '', 'role_supervisro');


INSERT INTO user_role VALUES (1, 1);


ALTER TABLE ONLY addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);



ALTER TABLE ONLY confirmation_token
    ADD CONSTRAINT confirmation_token_pkey PRIMARY KEY (id);



ALTER TABLE ONLY contacts
    ADD CONSTRAINT contacts_pkey PRIMARY KEY (id);



ALTER TABLE ONLY contracts
    ADD CONSTRAINT contracts_pkey PRIMARY KEY (id);



ALTER TABLE ONLY files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);



ALTER TABLE ONLY institutions
    ADD CONSTRAINT institutions_pkey PRIMARY KEY (id);




ALTER TABLE ONLY invoices
    ADD CONSTRAINT invoices_pkey PRIMARY KEY (id);



ALTER TABLE ONLY lessons
    ADD CONSTRAINT lessons_pkey PRIMARY KEY (id);



ALTER TABLE ONLY moduls
    ADD CONSTRAINT moduls_pkey PRIMARY KEY (id);



ALTER TABLE ONLY parents
    ADD CONSTRAINT parents_pkey PRIMARY KEY (id);



ALTER TABLE ONLY permission_role
    ADD CONSTRAINT permission_role_pkey PRIMARY KEY (role_id, permission_id);



ALTER TABLE ONLY permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);



ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);



ALTER TABLE ONLY signatures
    ADD CONSTRAINT signatures_pkey PRIMARY KEY (id);



ALTER TABLE ONLY students
    ADD CONSTRAINT students_pkey PRIMARY KEY (id);



ALTER TABLE ONLY supervisors
    ADD CONSTRAINT supervisors_pkey PRIMARY KEY (id);



ALTER TABLE ONLY teacher_student
    ADD CONSTRAINT teacher_student_pkey PRIMARY KEY (teacher_id, student_id);


ALTER TABLE ONLY teachers
    ADD CONSTRAINT teachers_pkey PRIMARY KEY (id);



ALTER TABLE ONLY tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (id);



ALTER TABLE ONLY users
    ADD CONSTRAINT uk_username UNIQUE (username);



ALTER TABLE ONLY files
    ADD CONSTRAINT ukivu9fdlcf2a9jsl1wgj6gbv7y UNIQUE (full_path, storage_type);


ALTER TABLE ONLY moduls
    ADD CONSTRAINT ukk7gvy9wd94q6kaqo3ky1cnpoj UNIQUE (contract_id, modul);


ALTER TABLE ONLY files
    ADD CONSTRAINT unique_fullpath_storagetype UNIQUE (full_path, storage_type);


ALTER TABLE ONLY user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);



ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


CREATE INDEX idx_name_roles ON roles USING btree (name);



CREATE INDEX idx_token_tokens ON tokens USING btree (token);



CREATE INDEX idx_user_id_tokens ON tokens USING btree (user_id);


CREATE INDEX idx_username_users ON users USING btree (username);




ALTER TABLE ONLY confirmation_token
    ADD CONSTRAINT confirmation_token_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY contracts
    ADD CONSTRAINT contracts_student_id_fkey FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY signatures
    ADD CONSTRAINT fk6d3mbois0nf3yk9e61rdapufo FOREIGN KEY (lesson_id) REFERENCES lessons(id);



ALTER TABLE ONLY students
    ADD CONSTRAINT fk_parent_student FOREIGN KEY (parent_id) REFERENCES parents(id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE ONLY permission_role
    ADD CONSTRAINT fk_permission_role_permissions FOREIGN KEY (permission_id) REFERENCES permissions(id);



ALTER TABLE ONLY permission_role
    ADD CONSTRAINT fk_permission_role_roles FOREIGN KEY (role_id) REFERENCES roles(id);


ALTER TABLE ONLY tokens
    ADD CONSTRAINT fk_tokens_users FOREIGN KEY (user_id) REFERENCES users(id);


ALTER TABLE ONLY user_role
    ADD CONSTRAINT fk_user_role_roles FOREIGN KEY (role_id) REFERENCES roles(id);


ALTER TABLE ONLY user_role
    ADD CONSTRAINT fk_user_role_users FOREIGN KEY (user_id) REFERENCES users(id);


ALTER TABLE ONLY contracts
    ADD CONSTRAINT fkc4rwpitwmdksdm08ll7ogxqhk FOREIGN KEY (contact_id) REFERENCES contacts(id);

ALTER TABLE ONLY contracts
    ADD CONSTRAINT fkh3d4256j3dvy9npmtw7on8nlt FOREIGN KEY (institution_id) REFERENCES institutions(id);

ALTER TABLE ONLY contacts
    ADD CONSTRAINT fki1rcporff7129nnoh9ikpamwv FOREIGN KEY (institution_id) REFERENCES institutions(id);


ALTER TABLE ONLY institutions
    ADD CONSTRAINT institutions_address_id_fkey FOREIGN KEY (address_id) REFERENCES addresses(id);


ALTER TABLE ONLY invoices
    ADD CONSTRAINT invoices_student_id_fkey FOREIGN KEY (student_id) REFERENCES students(id);


ALTER TABLE ONLY lessons
    ADD CONSTRAINT lessons_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON UPDATE CASCADE ON DELETE SET NULL;


ALTER TABLE ONLY moduls
    ADD CONSTRAINT moduls_fk FOREIGN KEY (contract_id) REFERENCES contracts(id) ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY student_lessons
    ADD CONSTRAINT student_lessons_lesson_id_fkey FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON UPDATE CASCADE ON DELETE CASCADE;




ALTER TABLE ONLY student_lessons
    ADD CONSTRAINT student_lessons_student_id_fkey FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE SET NULL;



ALTER TABLE ONLY students
    ADD CONSTRAINT students_id_fkey FOREIGN KEY (id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY supervisors
    ADD CONSTRAINT supervisors_id_fkey FOREIGN KEY (id) REFERENCES users(id);




ALTER TABLE ONLY teacher_student
    ADD CONSTRAINT teacher_student_student_id_fkey FOREIGN KEY (student_id) REFERENCES students(id) ON UPDATE CASCADE ON DELETE CASCADE;




ALTER TABLE ONLY teacher_student
    ADD CONSTRAINT teacher_student_teacher_id_fkey FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE ONLY teachers
    ADD CONSTRAINT teachers_id_fkey FOREIGN KEY (id) REFERENCES users(id) ON UPDATE SET NULL ON DELETE SET NULL;


ALTER TABLE ONLY users
    ADD CONSTRAINT users_address_id_fkey FOREIGN KEY (address_id) REFERENCES addresses(id);











CREATE VIEW admin_statistics AS
SELECT row_number() OVER () AS id,
       ( SELECT count(*) AS count
         FROM (teachers t
             LEFT JOIN users u ON ((t.id = u.id)))
         WHERE (u.marked_for_deletion = false)) AS teachers_number,
       ( SELECT count(*) AS count
         FROM (teachers t
             LEFT JOIN users u ON ((t.id = u.id)))
         WHERE ((u.marked_for_deletion = false) AND (NOT (EXISTS ( SELECT ts.teacher_id
                                                                   FROM teacher_student ts
                                                                   WHERE (ts.teacher_id = t.id)))))) AS teacher_without_student,
       ( SELECT count(*) AS count
         FROM (students s
             LEFT JOIN users u ON ((s.id = u.id)))
         WHERE (u.marked_for_deletion = false)) AS students_number,
       ( SELECT count(*) AS count
         FROM (students s
             LEFT JOIN users u ON ((s.id = u.id)))
         WHERE ((u.marked_for_deletion = false) AND (NOT (EXISTS ( SELECT ts.student_id
                                                                   FROM teacher_student ts
                                                                   WHERE (ts.student_id = s.id)))))) AS student_without_teacher,
       ( SELECT count(*) AS count
         FROM contracts) AS contracts_number,
       ( SELECT COALESCE(sum(m.units), (0)::double precision) AS "coalesce"
         FROM (contracts c
             JOIN moduls m ON ((c.id = m.contract_id)))
         WHERE (c.status = 0)) AS target_units_in_week,
       ( SELECT count(*) AS count
         FROM contracts
         WHERE (contracts.status = 0)) AS active_contracts,
       ( SELECT count(*) AS count
         FROM contracts
         WHERE (contracts.status = 1)) AS inactive_contracts,
       ( SELECT count(*) AS count
         FROM contracts
         WHERE (contracts.status = 2)) AS terminated_contracts,
       ( SELECT count(*) AS count
         FROM contracts
         WHERE (contracts.status = 3)) AS blocked_contracts,
       ( SELECT count(*) AS count
         FROM contracts
         WHERE (contracts.status = 5)) AS in_progress_contracts,
       ( SELECT count(*) AS count
         FROM lessons lessons_1) AS given_lessons,
       COALESCE(sum(
                        CASE
                            WHEN ((lessons.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND (lessons.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) + '7 days'::interval))) THEN lessons.units
                            ELSE (0)::double precision
                            END), (0)::double precision) AS given_units_in_week,
       count(
               CASE
                   WHEN ((lessons.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND (lessons.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) + '7 days'::interval))) THEN 1
                   ELSE NULL::integer
                   END) AS given_lessons_in_week,
       COALESCE(sum(
                        CASE
                            WHEN ((lessons.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND (lessons.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) + '1 mon'::interval))) THEN lessons.units
                            ELSE (0)::double precision
                            END), (0)::double precision) AS given_units_in_month,
       count(
               CASE
                   WHEN ((lessons.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND (lessons.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) + '1 mon'::interval))) THEN 1
                   ELSE NULL::integer
                   END) AS given_lessons_in_month
FROM lessons;


CREATE OR REPLACE VIEW lessons_table AS
SELECT l.id,
       l.teacher_id,
       l.modul,
       (((t.first_name)::text || ' '::text) || (t.last_name)::text) AS teacher,
       string_agg((((u.first_name)::text || ' '::text) || (u.last_name)::text), '; '::text) AS students,
       CASE
           WHEN (count(s.id) > 0) THEN 'Ja'::text
           ELSE 'Nein'::text
           END AS is_signed
FROM ((((lessons l
    LEFT JOIN student_lessons sl ON ((l.id = sl.lesson_id)))
    LEFT JOIN users u ON ((u.id = sl.student_id)))
    LEFT JOIN users t ON ((t.id = l.teacher_id)))
    LEFT JOIN signatures s ON ((s.lesson_id = l.id)))
GROUP BY l.id, l.modul, t.first_name, t.last_name;




CREATE VIEW students_lessons_statistics AS
SELECT sl.student_id AS id,
       count(l.*) AS taken_lessons,
       COALESCE(sum(l.units), (0)::double precision) AS taken_units,
       COALESCE(sum(
                        CASE
                            WHEN ((l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) + '7 days'::interval))) THEN l.units
                            ELSE (0)::double precision
                            END), (0)::double precision) AS taken_units_in_week,
       count(
               CASE
                   WHEN ((l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) + '7 days'::interval))) THEN 1
                   ELSE NULL::integer
                   END) AS taken_lessons_in_week,
       COALESCE(sum(
                        CASE
                            WHEN ((l.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) + '1 mon'::interval))) THEN l.units
                            ELSE (0)::double precision
                            END), (0)::double precision) AS taken_units_in_month,
       count(
               CASE
                   WHEN ((l.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) + '1 mon'::interval))) THEN 1
                   ELSE NULL::integer
                   END) AS taken_lessons_in_month
FROM (student_lessons sl
    LEFT JOIN lessons l ON ((sl.lesson_id = l.id)))
GROUP BY sl.student_id;



CREATE VIEW students_target_units AS
SELECT c.student_id,
       COALESCE(sum(m.units), (0)::double precision) AS target_units
FROM (contracts c
    JOIN moduls m ON ((c.id = m.contract_id)))
GROUP BY c.student_id;



CREATE VIEW students_statistics AS
SELECT ss.id,
       ss.taken_lessons,
       ss.taken_units,
       ss.taken_units_in_week,
       ss.taken_lessons_in_week,
       ss.taken_units_in_month,
       ss.taken_lessons_in_month,
       tu.target_units
FROM (students_lessons_statistics ss
    LEFT JOIN students_target_units tu ON ((ss.id = tu.student_id)));


CREATE VIEW teacher_student_contract_modul_summary AS
SELECT ts.teacher_id AS id,
       count(DISTINCT ts.student_id) AS students_number,
       COALESCE(sum(m.units), (0)::double precision) AS target_units_in_week
FROM ((teacher_student ts
    LEFT JOIN contracts c ON ((ts.student_id = c.student_id)))
    LEFT JOIN moduls m ON ((c.id = m.contract_id)))
GROUP BY ts.teacher_id;



CREATE VIEW teachers_lessons_statistics AS
SELECT l.teacher_id AS id,
       count(l.*) AS given_lessons,
       COALESCE(sum(l.units), (0)::double precision) AS given_units,
       COALESCE(sum(
                        CASE
                            WHEN ((l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) + '7 days'::interval))) THEN l.units
                            ELSE (0)::double precision
                            END), (0)::double precision) AS given_units_in_week,
       count(
               CASE
                   WHEN ((l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) + '7 days'::interval))) THEN 1
                   ELSE NULL::integer
                   END) AS given_lessons_in_week,
       COALESCE(sum(
                        CASE
                            WHEN ((l.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) + '1 mon'::interval))) THEN l.units
                            ELSE (0)::double precision
                            END), (0)::double precision) AS given_units_in_month,
       count(
               CASE
                   WHEN ((l.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) + '1 mon'::interval))) THEN 1
                   ELSE NULL::integer
                   END) AS given_lessons_in_month
FROM lessons l
GROUP BY l.teacher_id;



CREATE VIEW teachers_statistics AS
SELECT a.id,
       a.students_number,
       a.target_units_in_week,
       b.given_lessons,
       b.given_units_in_week,
       b.given_lessons_in_week,
       b.given_units_in_month,
       b.given_lessons_in_month,
       b.given_units
FROM (teacher_student_contract_modul_summary a
    LEFT JOIN teachers_lessons_statistics b ON ((a.id = b.id)));
