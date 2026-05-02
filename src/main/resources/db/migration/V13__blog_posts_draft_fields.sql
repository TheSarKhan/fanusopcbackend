ALTER TABLE blog_posts ADD COLUMN draft_title       VARCHAR(500);
ALTER TABLE blog_posts ADD COLUMN draft_content     TEXT;
ALTER TABLE blog_posts ADD COLUMN draft_cover_image_url VARCHAR(1024);
ALTER TABLE blog_posts ADD COLUMN draft_excerpt     TEXT;
ALTER TABLE blog_posts ADD COLUMN has_pending_draft BOOLEAN NOT NULL DEFAULT FALSE;
