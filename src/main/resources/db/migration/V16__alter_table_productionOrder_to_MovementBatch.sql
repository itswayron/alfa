ALTER TABLE production_order RENAME TO movement_batch;

ALTER TABLE movement_batch
      ADD COLUMN observation TEXT,
      ALTER COLUMN business_partner_id DROP NOT NULL,
      ALTER COLUMN date SET DEFAULT NOW();
