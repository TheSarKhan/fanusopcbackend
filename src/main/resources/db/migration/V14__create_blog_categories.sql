CREATE TABLE blog_categories (
    id         BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    color      VARCHAR(30)  NOT NULL DEFAULT '#002147',
    bg         VARCHAR(30)  NOT NULL DEFAULT '#E0EBF7',
    emoji      VARCHAR(10)  NOT NULL DEFAULT '📝',
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    sort_order INT          NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

INSERT INTO blog_categories (name, color, bg, emoji, sort_order) VALUES
  ('Stress',                '#7c4f1e', '#fef0e0', '😰', 1),
  ('Narahatlıq',            '#1e4d7c', '#e0eefe', '😟', 2),
  ('Depressiya',            '#4a1e7c', '#ede0fe', '😔', 3),
  ('Münasibətlər',          '#1e7c4a', '#e0fee8', '💑', 4),
  ('Özünüinkişaf',          '#7c6b1e', '#fef8e0', '🌱', 5),
  ('Uşaq psixologiyası',    '#7c1e4a', '#fee0ee', '🧒', 6);
