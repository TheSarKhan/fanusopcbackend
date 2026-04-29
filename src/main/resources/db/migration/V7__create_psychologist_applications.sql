CREATE TABLE psychologist_applications (
    id                BIGSERIAL PRIMARY KEY,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    email             VARCHAR(255) NOT NULL,
    phone             VARCHAR(50),
    password_hash     VARCHAR(255) NOT NULL,
    university        VARCHAR(255) NOT NULL,
    degree            VARCHAR(255) NOT NULL,
    graduation_year   VARCHAR(10)  NOT NULL,
    diploma_number    VARCHAR(100),
    diploma_file_url  VARCHAR(512),
    specializations   TEXT,
    session_types     TEXT,
    experience_years  VARCHAR(50),
    bio               TEXT,
    certifications    TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    admin_note        TEXT,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    reviewed_at       TIMESTAMP
);

CREATE INDEX idx_psy_app_email  ON psychologist_applications(email);
CREATE INDEX idx_psy_app_status ON psychologist_applications(status);
