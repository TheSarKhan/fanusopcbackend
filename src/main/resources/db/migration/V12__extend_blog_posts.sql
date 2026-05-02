ALTER TABLE blog_posts
    ADD COLUMN IF NOT EXISTS content          TEXT,
    ADD COLUMN IF NOT EXISTS cover_image_url  VARCHAR(1024),
    ADD COLUMN IF NOT EXISTS status           VARCHAR(20)  NOT NULL DEFAULT 'PUBLISHED',
    ADD COLUMN IF NOT EXISTS author_id        BIGINT       REFERENCES users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS updated_at       TIMESTAMP;

CREATE TABLE IF NOT EXISTS article_attachments (
    id               BIGSERIAL PRIMARY KEY,
    article_id       BIGINT       NOT NULL REFERENCES blog_posts(id) ON DELETE CASCADE,
    file_url         VARCHAR(1024) NOT NULL,
    file_name        VARCHAR(500)  NOT NULL,
    file_type        VARCHAR(50)   NOT NULL,  -- IMAGE, VIDEO, DOCUMENT
    file_size_bytes  BIGINT,
    display_order    INT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
