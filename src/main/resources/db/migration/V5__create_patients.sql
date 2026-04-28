-- Patients table
CREATE TABLE patients (
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    date_of_birth DATE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ─── Test/Dev seed accounts (all passwords: Fanus@2024) ───────────────────────
-- bcrypt hash for "Fanus@2024": $2b$12$BrZQaTR8FXbxd2izFlacIO0fYLPugPxiTB.wwsySu3aRWji7tq6W6

-- OPERATOR
INSERT INTO users (email, password, role, first_name, last_name, email_verified)
VALUES ('operator@fanus.az',
        '$2b$12$BrZQaTR8FXbxd2izFlacIO0fYLPugPxiTB.wwsySu3aRWji7tq6W6',
        'OPERATOR', 'Test', 'Operator', TRUE);

-- PATIENT
INSERT INTO users (email, password, role, first_name, last_name, phone, email_verified)
VALUES ('patient@fanus.az',
        '$2b$12$BrZQaTR8FXbxd2izFlacIO0fYLPugPxiTB.wwsySu3aRWji7tq6W6',
        'PATIENT', 'Test', 'Pasiyent', '+994501234567', TRUE);

INSERT INTO patients (user_id)
SELECT id FROM users WHERE email = 'patient@fanus.az';

-- PSYCHOLOGIST (user account; psychologist profile link added in Phase 3 / V8)
INSERT INTO users (email, password, role, first_name, last_name, email_verified)
VALUES ('psychologist@fanus.az',
        '$2b$12$BrZQaTR8FXbxd2izFlacIO0fYLPugPxiTB.wwsySu3aRWji7tq6W6',
        'PSYCHOLOGIST', 'Test', 'Psixoloq', TRUE);
