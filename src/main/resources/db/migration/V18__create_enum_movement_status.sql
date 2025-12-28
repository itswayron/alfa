-- Drop foreign key constraint from 'movement' table
ALTER TABLE movement DROP CONSTRAINT IF EXISTS fk_movement_status_id;

-- Drop the 'movement_status' table
DROP TABLE IF EXISTS movement_status CASCADE;

-- Drop the enum type if it already exists (for idempotency during development)
DROP TYPE IF EXISTS movement_status_enum;

-- Create the new ENUM type based on dev.weg.alfa.modules.models.simpleModels.MovementStatusEnum
CREATE TYPE movement_status_enum AS ENUM (
    'COMPLETED',
    'CANCELLED',
    'SCHEDULED',
    'REQUESTED',
    'QUOTATION',
    'PENDING',
    'AWAITING_DELIVERY',
    'AWAITING_INVOICE_ENTRY'
);

-- Add a temporary column to 'movement' table with the new ENUM type
ALTER TABLE movement ADD COLUMN status_enum movement_status_enum;

-- Update the new 'status_enum' column with values mapped from the old 'status_id'
-- IMPORTANT: This mapping assumes a direct correspondence between old integer IDs and new enum values.
-- You might need to adjust the CASE statement based on your actual data in the 'movement_status' table.
UPDATE movement
SET status_enum = CASE status_id
    WHEN 1 THEN 'COMPLETED'::movement_status_enum
    WHEN 2 THEN 'CANCELLED'::movement_status_enum
    WHEN 3 THEN 'SCHEDULED'::movement_status_enum
    WHEN 4 THEN 'REQUESTED'::movement_status_enum
    WHEN 5 THEN 'QUOTATION'::movement_status_enum
    WHEN 6 THEN 'PENDING'::movement_status_enum
    WHEN 7 THEN 'AWAITING_DELIVERY'::movement_status_enum
    WHEN 8 THEN 'AWAITING_INVOICE_ENTRY'::movement_status_enum
    ELSE NULL -- Consider how to handle existing rows with unmapped status_id values
END;

-- Set the new column as NOT NULL if the original 'status_id' was NOT NULL
ALTER TABLE movement ALTER COLUMN status_enum SET NOT NULL;

-- Drop the old 'status_id' column
ALTER TABLE movement DROP COLUMN status_id;

