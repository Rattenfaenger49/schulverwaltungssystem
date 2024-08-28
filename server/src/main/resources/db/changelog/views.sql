-- public.admin_statistic source

CREATE OR REPLACE VIEW public.admin_statistic
AS SELECT row_number() OVER () AS id,
          ( SELECT count(*) AS count
            FROM teachers t
            WHERE t.education::text <> 'DELETED'::text) AS teachers_number,
          ( SELECT count(*) AS count
            FROM teachers t
            WHERE t.education::text <> 'DELETED'::text AND NOT (EXISTS ( SELECT ts.teacher_id
                   FROM teacher_student ts
                  WHERE ts.teacher_id = t.id))) AS teacher_without_student,
          ( SELECT count(*) AS count
            FROM students s
            WHERE s.level::text <> 'DELETED'::text) AS students_number,
          ( SELECT count(*) AS count
            FROM students s
            WHERE s.level::text <> 'DELETED'::text AND NOT (EXISTS ( SELECT ts.student_id
                   FROM teacher_student ts
                  WHERE ts.student_id = s.id))) AS student_without_teacher,
          ( SELECT count(*) AS count
            FROM contracts) AS contracts_number,
          ( SELECT COALESCE(sum(m.units), 0::double precision) AS "coalesce"
            FROM contracts c
                     JOIN moduls m ON c.id = m.contract_id
            WHERE c.status::text = 'ACTIVE'::text) AS target_units_in_week,
          ( SELECT count(*) AS count
            FROM contracts
            WHERE contracts.status::text = 'ACTIVE'::text) AS active_contracts,
          ( SELECT count(*) AS count
            FROM contracts
            WHERE contracts.status::text = 'INACTIVE'::text) AS inactive_contracts,
          ( SELECT count(*) AS count
            FROM contracts
            WHERE contracts.status::text = 'TERMINATED'::text) AS terminated_contracts,
          ( SELECT count(*) AS count
            FROM contracts
            WHERE contracts.status::text = 'BLOCKED'::text) AS blocked_contracts,
          ( SELECT count(*) AS count
            FROM lessons lessons_1) AS given_lessons,
          COALESCE(sum(
                           CASE
                               WHEN lessons.start_at >= date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) AND lessons.start_at < (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'::interval) THEN lessons.units
                               ELSE 0::double precision
                           END), 0::double precision) AS given_units_in_week,
          count(
                  CASE
                      WHEN lessons.start_at >= date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) AND lessons.start_at < (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'::interval) THEN 1
                      ELSE NULL::integer
                  END) AS given_lessons_in_week,
          COALESCE(sum(
                           CASE
                               WHEN lessons.start_at >= date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) AND lessons.start_at < (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'::interval) THEN lessons.units
                               ELSE 0::double precision
                           END), 0::double precision) AS given_units_in_month,
          count(
                  CASE
                      WHEN lessons.start_at >= date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) AND lessons.start_at < (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'::interval) THEN 1
                      ELSE NULL::integer
                  END) AS given_lessons_in_month
   FROM lessons;
-- public.admin_statistik source END


-- public.lessons_table source

CREATE OR REPLACE VIEW public.lessons_table
AS SELECT l.id,
          l.teacher_id,
          l.modul,
          (t.first_name::text || ' '::text) || t.last_name::text AS teacher,
           string_agg((u.first_name::text || ' '::text) || u.last_name::text, '; '::text) AS students,
          CASE
              WHEN l.signature IS NULL THEN false
              ELSE true
              END AS has_signature
   FROM lessons l
            LEFT JOIN student_lessons sl ON l.id = sl.lesson_id
            LEFT JOIN users u ON u.id = sl.student_id
            LEFT JOIN users t ON t.id = l.teacher_id
   GROUP BY l.id, l.modul, t.first_name, t.last_name;


-- teahcers views
-- public.teachers_lessons_statistics source

CREATE OR REPLACE VIEW public.teachers_lessons_statistics
AS SELECT l.teacher_id as id,
          count(l.*) AS given_lessons,
          COALESCE(sum(
                           CASE
                               WHEN l.start_at >= date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) and
                                    l.start_at < (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'::interval) THEN l.units
                               ELSE 0::double precision
                           END), 0::double precision) AS given_units_in_week,
          count(
                  CASE
                      WHEN l.start_at >= date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) and
                           l.start_at < (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'::interval) THEN 1
                      ELSE NULL::integer
                  END) AS given_lessons_in_week,
          COALESCE(sum(
                           CASE
                               WHEN l.start_at >= date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) and
                                    l.start_at < (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'::interval) THEN l.units
                               ELSE 0::double precision
                           END), 0::double precision) AS given_units_in_month,
          count(
                  CASE
                      WHEN l.start_at >= date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) and
                           l.start_at < (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'::interval) THEN 1
                      ELSE NULL::integer
                  END) AS given_lessons_in_month
   FROM lessons l
   GROUP BY l.teacher_id;



-- public.teacher_student_contract_modul_summary source

CREATE OR REPLACE VIEW public.teacher_student_contract_modul_summary
AS SELECT ts.teacher_id as id,
          count(DISTINCT ts.student_id) AS students_number,
          COALESCE(sum(m.units), 0::double precision) AS target_units_in_week
   FROM teacher_student ts
            LEFT JOIN contracts c ON ts.student_id = c.student_id
            LEFT JOIN moduls m ON c.id = m.contract_id
   GROUP BY ts.teacher_id;





CREATE OR REPLACE VIEW public.teachers_statistics AS
SELECT
    a.*,
    b.given_lessons,
    b.given_units_in_week,
    b.given_lessons_in_week,
    b.given_units_in_month,
    b.given_lessons_in_month
FROM
    teacher_student_contract_modul_summary a
        LEFT JOIN
    teachers_lessons_statistics b ON a.id = b.id;
-- end teachers views
