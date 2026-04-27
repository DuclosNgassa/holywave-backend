-- Full-text index
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Full-text
CREATE INDEX IF NOT EXISTS idx_post_search ON post
    USING GIN (
               to_tsvector('simple', title || ' ' || description || ' ' || city || ' ' || state)
        );

-- Fuzzy
CREATE INDEX IF NOT EXISTS idx_post_title_trgm ON post USING GIN (title gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_post_desc_trgm ON post USING GIN (description gin_trgm_ops);

-- Join table
CREATE INDEX IF NOT EXISTS idx_post_category_post ON post_category(post_id);
CREATE INDEX IF NOT EXISTS idx_post_category_cat ON post_category(category_id);