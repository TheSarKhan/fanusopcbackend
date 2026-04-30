ALTER TABLE psychologists
    ADD COLUMN IF NOT EXISTS bio             TEXT,
    ADD COLUMN IF NOT EXISTS phone           VARCHAR(50),
    ADD COLUMN IF NOT EXISTS email           VARCHAR(255),
    ADD COLUMN IF NOT EXISTS languages       TEXT,
    ADD COLUMN IF NOT EXISTS session_types   TEXT,
    ADD COLUMN IF NOT EXISTS activity_format VARCHAR(100),
    ADD COLUMN IF NOT EXISTS university      VARCHAR(255),
    ADD COLUMN IF NOT EXISTS degree          VARCHAR(100),
    ADD COLUMN IF NOT EXISTS graduation_year VARCHAR(10);
