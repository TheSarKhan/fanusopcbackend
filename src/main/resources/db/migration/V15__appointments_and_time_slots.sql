-- ─── Phase 2: real appointment system + psychologist time slots ───────────────

-- Link psychologist profile to user account (approved psychologists own a user row)
ALTER TABLE psychologists
    ADD COLUMN IF NOT EXISTS user_id                  BIGINT REFERENCES users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS default_session_minutes  INT NOT NULL DEFAULT 50;

CREATE UNIQUE INDEX IF NOT EXISTS uq_psychologists_user_id
    ON psychologists(user_id) WHERE user_id IS NOT NULL;

-- Backfill: match psychologist profile to its user account by email
UPDATE psychologists p
SET user_id = u.id
FROM users u
WHERE u.role = 'PSYCHOLOGIST'
  AND LOWER(u.email) = LOWER(p.email)
  AND p.user_id IS NULL;

-- ─── Psychologist weekly recurring time slots ────────────────────────────────
CREATE TABLE IF NOT EXISTS psychologist_time_slots (
    id              BIGSERIAL PRIMARY KEY,
    psychologist_id BIGINT NOT NULL REFERENCES psychologists(id) ON DELETE CASCADE,
    day_of_week     INT    NOT NULL CHECK (day_of_week BETWEEN 1 AND 7), -- ISO: 1=Mon..7=Sun
    start_time      TIME   NOT NULL,
    end_time        TIME   NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CHECK (start_time < end_time)
);
CREATE INDEX IF NOT EXISTS idx_pts_psychologist_active
    ON psychologist_time_slots(psychologist_id, active);

-- ─── Date-specific overrides (block a day, or add an extra slot) ─────────────
CREATE TABLE IF NOT EXISTS psychologist_time_slot_overrides (
    id              BIGSERIAL PRIMARY KEY,
    psychologist_id BIGINT NOT NULL REFERENCES psychologists(id) ON DELETE CASCADE,
    override_date   DATE   NOT NULL,
    override_type   VARCHAR(20) NOT NULL CHECK (override_type IN ('BLOCK','EXTRA')),
    start_time      TIME,
    end_time        TIME,
    note            VARCHAR(255),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    CHECK (override_type = 'BLOCK'
        OR (start_time IS NOT NULL AND end_time IS NOT NULL AND start_time < end_time))
);
CREATE INDEX IF NOT EXISTS idx_ptso_psychologist_date
    ON psychologist_time_slot_overrides(psychologist_id, override_date);

-- ─── Appointment refactor: relational FKs + concrete time window ─────────────
ALTER TABLE appointments
    ALTER COLUMN patient_name DROP NOT NULL,
    ALTER COLUMN phone DROP NOT NULL;

ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS patient_id                BIGINT REFERENCES patients(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS psychologist_id           BIGINT REFERENCES psychologists(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS requested_psychologist_id BIGINT REFERENCES psychologists(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS assigned_by_operator_id   BIGINT REFERENCES users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS requested_start_at        TIMESTAMP,
    ADD COLUMN IF NOT EXISTS start_at                  TIMESTAMP,
    ADD COLUMN IF NOT EXISTS end_at                    TIMESTAMP,
    ADD COLUMN IF NOT EXISTS session_format            VARCHAR(20),
    ADD COLUMN IF NOT EXISTS operator_note             TEXT,
    ADD COLUMN IF NOT EXISTS updated_at                TIMESTAMP;

ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_status_check;
ALTER TABLE appointments ADD CONSTRAINT appointments_status_check
    CHECK (status IN ('PENDING','ASSIGNED','CONFIRMED','COMPLETED','CANCELLED','REJECTED','NEW','IN_REVIEW','NO_SHOW'));

ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_session_format_check;
ALTER TABLE appointments ADD CONSTRAINT appointments_session_format_check
    CHECK (session_format IS NULL OR session_format IN ('ONLINE','IN_PERSON'));

CREATE INDEX IF NOT EXISTS idx_appointments_status            ON appointments(status);
CREATE INDEX IF NOT EXISTS idx_appointments_psychologist_id   ON appointments(psychologist_id);
CREATE INDEX IF NOT EXISTS idx_appointments_patient_id        ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_appointments_start_at          ON appointments(start_at);
