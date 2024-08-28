-- public.lessons_archives_view source

CREATE OR REPLACE VIEW public.lessons_archives_view
AS SELECT lv.id,
          lv.lesson_id,
          lv.modul_type,
          lv.start_at,
          lv.units,
          lv.lesson_type,
          lv.description,
          lv.comment,
          lv.contract_type,
          lv.students_ids,
          lv.teacher_id,
          lv.signatures_ids,
          lv.created_at,
          lv.created_by,
          string_agg((t.first_name::text || ' '::text) || t.last_name::text, ', '::text) AS teacher_name,
          string_agg((s.first_name::text || ' '::text) || s.last_name::text, ', '::text) AS student_names
   FROM lessons_versions lv
            JOIN LATERAL unnest(string_to_array(lv.students_ids::text, ', '::text)::bigint[]) student_id(student_id) ON true
            JOIN users s ON s.id = student_id.student_id
            JOIN users t ON t.id = lv.teacher_id
   GROUP BY lv.id
   ORDER BY lv.created_at DESC;