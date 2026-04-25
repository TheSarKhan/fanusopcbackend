-- Users
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(50)  NOT NULL DEFAULT 'ADMIN',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Psychologists
CREATE TABLE psychologists (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    title           VARCHAR(255) NOT NULL,
    experience      VARCHAR(50)  NOT NULL,
    sessions_count  VARCHAR(50)  NOT NULL,
    rating          VARCHAR(10)  NOT NULL,
    photo_url       TEXT,
    accent_color    VARCHAR(20)  NOT NULL DEFAULT '#3B6FA5',
    bg_color        VARCHAR(20)  NOT NULL DEFAULT '#EEF5FF',
    display_order   INT          NOT NULL DEFAULT 0,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE psychologist_specializations (
    psychologist_id BIGINT       NOT NULL REFERENCES psychologists(id) ON DELETE CASCADE,
    specialization  VARCHAR(255) NOT NULL
);

-- Stats
CREATE TABLE stats (
    id            BIGSERIAL PRIMARY KEY,
    stat_value    INT          NOT NULL,
    suffix        VARCHAR(10)  NOT NULL,
    label         VARCHAR(255) NOT NULL,
    sub_label     VARCHAR(255) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0
);

-- Announcements
CREATE TABLE announcements (
    id              BIGSERIAL PRIMARY KEY,
    category        VARCHAR(100) NOT NULL,
    category_color  VARCHAR(20)  NOT NULL DEFAULT '#3B6FA5',
    category_bg     VARCHAR(20)  NOT NULL DEFAULT '#E4EEF8',
    title           VARCHAR(500) NOT NULL,
    excerpt         TEXT         NOT NULL,
    published_date  DATE         NOT NULL,
    icon_type       VARCHAR(20)  NOT NULL DEFAULT 'STAR',
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Blog posts
CREATE TABLE blog_posts (
    id                  BIGSERIAL PRIMARY KEY,
    category            VARCHAR(100) NOT NULL,
    category_color      VARCHAR(20)  NOT NULL DEFAULT '#3B6FA5',
    category_bg         VARCHAR(20)  NOT NULL DEFAULT '#E4EEF8',
    title               VARCHAR(500) NOT NULL,
    excerpt             TEXT         NOT NULL,
    read_time_minutes   INT          NOT NULL DEFAULT 5,
    published_date      DATE         NOT NULL,
    emoji               VARCHAR(10)  NOT NULL DEFAULT '📝',
    slug                VARCHAR(255) NOT NULL UNIQUE,
    featured            BOOLEAN      NOT NULL DEFAULT FALSE,
    active              BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- FAQs
CREATE TABLE faqs (
    id            BIGSERIAL PRIMARY KEY,
    question      TEXT     NOT NULL,
    answer        TEXT     NOT NULL,
    display_order INT      NOT NULL DEFAULT 0,
    active        BOOLEAN  NOT NULL DEFAULT TRUE
);

-- Testimonials
CREATE TABLE testimonials (
    id          BIGSERIAL PRIMARY KEY,
    quote       TEXT         NOT NULL,
    author_name VARCHAR(255) NOT NULL,
    author_role VARCHAR(255) NOT NULL,
    initials    VARCHAR(5)   NOT NULL,
    gradient    VARCHAR(255) NOT NULL DEFAULT 'linear-gradient(135deg, #3B6FA5, #5A4FC8)',
    rating      INT          NOT NULL DEFAULT 5,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Site config
CREATE TABLE site_config (
    config_key   VARCHAR(100) PRIMARY KEY,
    config_value TEXT NOT NULL
);

-- Appointments
CREATE TABLE appointments (
    id                BIGSERIAL PRIMARY KEY,
    patient_name      VARCHAR(255) NOT NULL,
    phone             VARCHAR(50)  NOT NULL,
    psychologist_name VARCHAR(255),
    note              TEXT,
    preferred_date    DATE,
    status            VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);
