DROP INDEX IF EXISTS idx_teacher_invoices;
ALTER TABLE public.invoices DROP CONSTRAINT invoices_teacher_id_fkey;


ALTER TABLE public.invoices RENAME COLUMN teacher_id TO user_id;


ALTER TABLE public.invoices ADD CONSTRAINT invoices_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);



CREATE INDEX idx_user_invoices ON public.invoices USING btree (user_id);