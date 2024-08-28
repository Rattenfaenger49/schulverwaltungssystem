
ALTER TABLE invoices ADD COLUMN
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL;

ALTER TABLE invoices ADD COLUMN
    created_by varchar(256)  DEFAULT 'INIT_USER' NOT NULL;

ALTER TABLE invoices ADD  COLUMN
    due_date date NOT NULL DEFAULT CURRENT_DATE;

ALTER TABLE invoices ADD  COLUMN
    payment_method varchar(64) NULL;

ALTER TABLE invoices ADD  COLUMN
    discount_amount numeric(10, 2) NULL DEFAULT 0;


ALTER TABLE invoices ADD  COLUMN
    period_start_date date NOT NULL DEFAULT CURRENT_DATE;

ALTER TABLE invoices ADD  COLUMN
        period_end_date date NOT NULL DEFAULT CURRENT_DATE;

ALTER TABLE invoices ADD  COLUMN
    adjustments numeric(10, 2) NULL DEFAULT 0;