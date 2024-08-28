CREATE TABLE lessons_versions (
                                  id BIGSERIAL PRIMARY KEY,
                                  lesson_id BIGINT NOT NULL,
                                  modul_type VARCHAR(64) NOT NULL,
                                  start_at TIMESTAMPTZ NOT NULL,
                                  units DOUBLE PRECISION NOT NULL,
                                  lesson_type VARCHAR(64) NOT NULL,
                                  description VARCHAR(512) NOT NULL,
                                  comment VARCHAR(512),
                                  contract_type VARCHAR(64),
                                  students_ids VARCHAR(256),
                                  teacher_id BIGINT,
                                  signatures_ids VARCHAR(256),
                                  created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                  created_by VARCHAR(128),
                                  CONSTRAINT fk_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);
