
CREATE VIEW admin_lessons_statistic AS
SELECT row_number() OVER () AS id, count(l) AS given_lessons, COALESCE(sum(
    CASE
    WHEN (l.contract_type = 'WEEK') THEN 1
    ELSE 0
    END), 0) AS given_lessons_for_weekly_contracts, COALESCE(sum(
    CASE
    WHEN (l.contract_type = 'MONAT') THEN 1
    ELSE 0
    END), 0) AS given_lessons_for_monthly_contracts, COALESCE(sum(
    CASE
    WHEN (l.contract_type = 'PERIOD') THEN 1
    ELSE 0
    END), 0) AS given_lessons_for_period_contracts, COALESCE(sum(
    CASE
    WHEN (l.contract_type = 'INDIVIDUALLY') THEN 1
    ELSE 0
    END), 0) AS given_lessons_for_individual_contracts, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'WEEK') AND
    (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
    '7 days'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_weekly_contracts_in_week, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'MONAT') AND
    (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
    '7 days'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_monthly_contracts_in_week, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'PERIOD') AND
    (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
    '7 days'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_period_contracts_in_week, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'INDIVIDUALLY') AND
    (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
    '7 days'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_individual_contracts_in_week, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'WEEK') AND (l.start_at >=
    date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
    '1 mon'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_weekly_contracts_in_month, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'MONAT') AND (l.start_at >=
    date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
    '1 mon'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_monthly_contracts_in_month, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'PERIOD') AND (l.start_at >=
    date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
    '1 mon'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_period_contracts_in_month, COALESCE(sum(
    CASE
    WHEN ((l.contract_type = 'INDIVIDUALLY') AND (l.start_at >=
    date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
    '1 mon'::interval))) THEN l.units
    ELSE 0
    END), 0) AS given_units_for_individual_contracts_in_month, COALESCE(sum(
    CASE
    WHEN ((l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
    '7 days'::interval))) THEN 1
    ELSE 0
    END), 0) AS given_lessons_in_week, COALESCE(sum(
    CASE
    WHEN (
    (l.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
    (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
    '1 mon'::interval))) THEN 1
    ELSE 0
    END), 0) AS given_lessons_in_month, COALESCE(sum(l.units), 0) AS given_units
FROM lessons l;



CREATE VIEW admin_students_statistic AS
SELECT row_number() OVER () AS id, count(s) FILTER (WHERE (u.marked_for_deletion = false)) AS students_number,
       count(s) FILTER (WHERE (u.marked_for_deletion = true)) AS marked_for_deletion_students,
       count(s) FILTER (WHERE (NOT (u.id IN (SELECT ts.student_id
    FROM teachers_students ts)))) AS students_without_teachers,
    count(s) FILTER (WHERE (NOT (u.id IN (SELECT sl.student_id
    FROM students_lessons sl)))) AS students_without_lessons
FROM (students s
    LEFT JOIN users u ON ((s.id = u.id)));


CREATE VIEW admin_teachers_statistic AS
SELECT row_number() OVER () AS id, count(t) FILTER (WHERE (u.marked_for_deletion = false)) AS teachers_number,
       count(t) FILTER (WHERE (u.marked_for_deletion = true)) AS marked_for_deletion_teachers,
       count(t) FILTER (WHERE (NOT (u.id IN (SELECT ts.teacher_id
    FROM teachers_students ts)))) AS teachers_without_students,
    count(t) FILTER (WHERE (NOT (u.id IN (SELECT l.teacher_id
    FROM lessons l)))) AS teachers_without_lessons
FROM (teachers t
    LEFT JOIN users u ON ((t.id = u.id)));


CREATE VIEW admin_contracts_statistic AS
WITH summed_units AS (SELECT moduls.contract_id,
                             COALESCE(sum(moduls.units), 0) AS total_units
                      FROM moduls
                      GROUP BY moduls.contract_id)
SELECT row_number() OVER () AS id, count(c) AS contracts_number, COALESCE(sum(
    CASE
    WHEN (c.contract_type = 'WEEK') THEN 1
    ELSE 0
    END), 0) AS weekly_contracts_number, COALESCE(sum(
    CASE
    WHEN ((c.contract_type = 'WEEK') AND (c.status = 'ACTIVE')) THEN su.total_units
    ELSE 0
    END), 0) AS target_units_week, COALESCE(sum(
    CASE
    WHEN (c.contract_type = 'MONAT') THEN 1
    ELSE 0
    END), 0) AS monthly_contracts_number, COALESCE(sum(
    CASE
    WHEN ((c.contract_type = 'MONAT') AND (c.status = 'ACTIVE')) THEN su.total_units
    ELSE 0
    END), 0) AS target_units_month, COALESCE(sum(
    CASE
    WHEN (c.contract_type = 'PERIOD') THEN 1
    ELSE 0
    END), (0)::bigint) AS period_contracts_number, COALESCE(sum(
    CASE
    WHEN ((c.contract_type = 'PERIOD') AND (c.status = 'ACTIVE')) THEN su.total_units
    ELSE 0
    END), 0) AS target_units_period, COALESCE(sum(
    CASE
    WHEN (c.contract_type = 'INDIVIDUALLY') THEN 1
    ELSE 0
    END), 0) AS individual_contracts_number, COALESCE(sum(
    CASE
    WHEN (c.status = 'ACTIVE') THEN 1
    ELSE 0
    END), 0) AS active_contracts, COALESCE(sum(
    CASE
    WHEN (c.status = 'INACTIVE') THEN 1
    ELSE 0
    END), 0) AS inactive_contracts, COALESCE(sum(
    CASE
    WHEN (c.status = 'TERMINATED') THEN 1
    ELSE 0
    END), 0) AS terminated_contracts, COALESCE(sum(
    CASE
    WHEN (c.status = 'BLOCKED') THEN 1
    ELSE 0
    END), 0) AS blocked_contracts, COALESCE(sum(
    CASE
    WHEN (c.status = 'IN_PROGRESS') THEN 1
    ELSE 0
    END), 0) AS in_progress_contracts
FROM (contracts c
    LEFT JOIN summed_units su ON ((c.id = su.contract_id)));

CREATE VIEW admin_statistic AS
SELECT ads.id,
       ads.students_number,
       ads.marked_for_deletion_students,
       ads.students_without_teachers,
       ads.students_without_lessons,
       ats.teachers_number,
       ats.marked_for_deletion_teachers,
       ats.teachers_without_students,
       ats.teachers_without_lessons,
       als.given_lessons,
       als.given_lessons_for_weekly_contracts,
       als.given_lessons_for_monthly_contracts,
       als.given_lessons_for_period_contracts,
       als.given_lessons_for_individual_contracts,
       als.given_units_for_weekly_contracts_in_week,
       als.given_units_for_monthly_contracts_in_week,
       als.given_units_for_period_contracts_in_week,
       als.given_units_for_individual_contracts_in_week,
       als.given_units_for_weekly_contracts_in_month,
       als.given_units_for_monthly_contracts_in_month,
       als.given_units_for_period_contracts_in_month,
       als.given_units_for_individual_contracts_in_month,
       als.given_lessons_in_week,
       als.given_lessons_in_month,
       als.given_units,
       acs.contracts_number,
       acs.weekly_contracts_number,
       acs.target_units_week,
       acs.monthly_contracts_number,
       acs.target_units_month,
       acs.period_contracts_number,
       acs.target_units_period,
       acs.individual_contracts_number,
       acs.active_contracts,
       acs.inactive_contracts,
       acs.terminated_contracts,
       acs.blocked_contracts,
       acs.in_progress_contracts
FROM (((admin_students_statistic ads
    JOIN admin_teachers_statistic ats USING (id))
    JOIN admin_lessons_statistic als USING (id))
    JOIN admin_contracts_statistic acs USING (id));


CREATE VIEW teachers_lessons_statistic AS
SELECT t.id,
       COALESCE(sum(
                        CASE
                        WHEN(l.contract_type = 'WEEK') THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_weekly_contracts,
       COALESCE(sum(
                        CASE
                        WHEN(l.contract_type = 'MONAT') THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_monthly_contracts,
       COALESCE(sum(
                        CASE
                        WHEN(l.contract_type = 'PERIOD') THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_period_contracts,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'WEEK') AND
                             (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '7 days'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_weekly_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'MONAT') AND
                             (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '7 days'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_monthly_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'PERIOD') AND
                             (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '7 days'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_period_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'INDIVIDUALLY') AND
                             (l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '7 days'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_individual_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'WEEK') AND (l.start_at >=
                                                             date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '1 mon'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_weekly_contracts_in_month,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'MONAT') AND (l.start_at >=
                                                              date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '1 mon'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_monthly_contracts_in_month,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'PERIOD') AND (l.start_at >=
                                                               date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '1 mon'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_period_contracts_in_month,
       COALESCE(sum(
                        CASE
                        WHEN((l.contract_type = 'INDIVIDUALLY') AND (l.start_at >=
                                                                     date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                             (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) +
                                            '1 mon'::interval))) THEN l.units
                        ELSE 0
                        END), 0) AS given_units_for_individual_contracts_in_month,
       count(l)    AS given_lessons,
       count(
               CASE
               WHEN((l.start_at >= date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                    (l.start_at < (date_trunc('month'::text, (CURRENT_DATE)::timestamp with time zone) + '1 mon'::interval)))
               THEN 1
               ELSE NULL
               END)              AS given_lessons_in_month,
       count(
               CASE
               WHEN((l.start_at >= date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone)) AND
                    (l.start_at <
                     (date_trunc('week'::text, (CURRENT_DATE)::timestamp with time zone) + '7 days'::interval)))
               THEN 1
               ELSE NULL
               END)              AS given_lessons_in_week
FROM (teachers t
    LEFT JOIN lessons l ON ((t.id = l.teacher_id)))
GROUP BY t.id;
CREATE VIEW teachers_target_units_statistic AS
SELECT t.id,
       COALESCE(sum(m.units), 0::double precision) AS target_units,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'WEEK'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_week,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'MONAT'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_month,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'PERIOD'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_period,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'INDIVIDUALLY'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_individually
FROM teachers t left join teachers_students ts on t.id = ts.teacher_id
                LEFT JOIN contracts c ON c.student_id = ts.student_id
                LEFT JOIN moduls m ON c.id = m.contract_id
GROUP BY t.id;

CREATE VIEW teachers_statistic AS
SELECT ls.*,
       ts.target_units,
       ts.target_units_week,
       ts.target_units_month,
       ts.target_units_period,
       ts.target_units_individually
FROM (teachers_target_units_statistic ts FULL JOIN teachers_lessons_statistic ls ON ((ts.id = ls.id)));


CREATE VIEW students_contracts_statistic AS
SELECT s.id,
       COALESCE(sum(m.units), 0::double precision) AS target_units,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'WEEK'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_week,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'MONAT'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_month,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'PERIOD'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_period,
       COALESCE(sum(
                        CASE
                            WHEN c.contract_type::text = 'INDIVIDUALLY'::text THEN m.units
                            ELSE 0::double precision
                            END), 0::double precision) AS target_units_individually
FROM students s left join contracts c on s.id = c.student_id
                LEFT JOIN moduls m ON c.id = m.contract_id
GROUP BY s.id;


CREATE VIEW students_lessons_statistic AS
SELECT s.id,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'WEEK' ::text THEN l.units
                        ELSE 0 ::double precision
                        END), 0::double precision)                                                                                                               AS taken_units_week,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'MONAT' ::text THEN l.units
                        ELSE 0 ::double precision
                        END),
                0::double precision)                                                                                                                             AS taken_units_month,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'PERIOD' ::text THEN l.units
                        ELSE 0 ::double precision
                        END),
                0::double precision)                                                                                                                             AS taken_units_period,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'INDIVIDUALLY' ::text THEN l.units
                        ELSE 0 ::double precision
                        END),
                0::double precision)                                                                                                                             AS taken_units_individual,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'WEEK' ::text AND
                                                             l.start_at >=
                                                             date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) AND
                                                             l.start_at <
                                                             (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_weekly_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'MONAT' ::text AND
                                                              l.start_at >=
                                                              date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) AND
                                                              l.start_at <
                                                              (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_monthly_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'PERIOD' ::text AND
                                                               l.start_at >=
                                                               date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) AND
                                                               l.start_at <
                                                               (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_period_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'INDIVIDUALLY' ::text AND
                                                                     l.start_at >=
                                                                     date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) AND
                                                                     l.start_at <
                                                                     (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_individual_contracts_in_week,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'WEEK' ::text AND
                                                             l.start_at >=
                                                             date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) AND
                                                             l.start_at <
                                                             (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_weekly_contracts_in_month,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'MONAT' ::text AND
                                                              l.start_at >=
                                                              date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) AND
                                                              l.start_at <
                                                              (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_monthly_contracts_in_month,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'PERIOD' ::text AND
                                                               l.start_at >=
                                                               date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) AND
                                                               l.start_at <
                                                               (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_period_contracts_in_month,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'INDIVIDUALLY' ::text AND
                                                                     l.start_at >=
                                                                     date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) AND
                                                                     l.start_at <
                                                                     (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon'
                        ::interval)
                                THEN l.units
                            ELSE 0::double precision
                            END),
                0::double precision)                                                                                                                             AS taken_units_for_individual_contracts_in_month,
       COALESCE(sum(
                        CASE
                        WHEN l.start_at >= date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) AND
                             l.start_at <
                             (date_trunc('week'::text, CURRENT_DATE::timestamp with time zone) + '7 days' ::interval)
                                THEN 1
                            ELSE 0
                            END), 0::bigint) AS taken_lessons_in_week,
       COALESCE(sum(
                        CASE
                        WHEN l.start_at >= date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) AND
                             l.start_at <
                             (date_trunc('month'::text, CURRENT_DATE::timestamp with time zone) + '1 mon' ::interval)
                                THEN 1
                            ELSE 0
                            END), 0::bigint) AS taken_lessons_in_month,
       count(l)                                                                                                                                               AS taken_lessons,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'WEEK' THEN l.units
                        ELSE 0 ::double precision
                        END),
                0::double precision)                                                                                                                             AS taken_units_for_weekly_contracts,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'MONAT' THEN l.units
                        ELSE 0 ::double precision
                        END),
                0::double precision)                                                                                                                             AS taken_units_for_monthly_contracts,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'PERIOD' ::text THEN l.units
                        ELSE 0 ::double precision
                        END),
                0::double precision)                                                                                                                             AS taken_units_for_period_contracts,
       COALESCE(sum(
                        CASE
                        WHEN l.contract_type ::text = 'INDIVIDUALLY' ::text THEN l.units
                        ELSE 0 ::double precision
                        END),
                0::double precision)                                                                                                                             AS taken_units_for_individual_contracts
from students s
         left join students_lessons sl on s.id = sl.student_id
         LEFT JOIN lessons l ON sl.lesson_id = l.id
GROUP BY s.id;


CREATE OR REPLACE VIEW students_statistic
AS
SELECT
       b.target_units,
       b.target_units_week,
       b.target_units_month,
       b.target_units_period,
      a.*
FROM students_contracts_statistic b
         LEFT JOIN students_lessons_statistic a USING (id);
