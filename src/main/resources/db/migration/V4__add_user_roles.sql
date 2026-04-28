-- Extend users table for multi-role platform
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS first_name           VARCHAR(100),
    ADD COLUMN IF NOT EXISTS last_name            VARCHAR(100),
    ADD COLUMN IF NOT EXISTS phone                VARCHAR(30),
    ADD COLUMN IF NOT EXISTS email_verified       BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS email_verification_token  VARCHAR(255),
    ADD COLUMN IF NOT EXISTS verification_expires_at   TIMESTAMP,
    ADD COLUMN IF NOT EXISTS password_reset_token      VARCHAR(255),
    ADD COLUMN IF NOT EXISTS password_reset_expires_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS last_login           TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at           TIMESTAMP;

-- Drop old role constraint if exists, add new one
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check
    CHECK (role IN ('ADMIN', 'PATIENT', 'PSYCHOLOGIST', 'OPERATOR'));

-- Admin is pre-existing; mark as verified
UPDATE users SET email_verified = TRUE WHERE role = 'ADMIN';
