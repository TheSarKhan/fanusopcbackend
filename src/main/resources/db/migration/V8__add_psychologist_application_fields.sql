ALTER TABLE psychologist_applications
    ADD COLUMN IF NOT EXISTS languages TEXT,
    ADD COLUMN IF NOT EXISTS activity_format VARCHAR(20),
    ADD COLUMN IF NOT EXISTS certificate_file_urls TEXT;
