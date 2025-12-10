-- Add affiliate_id to users table to establish relationship between users and affiliates
-- This allows AFILIADO users to be linked to their affiliate record
ALTER TABLE users ADD COLUMN affiliate_id VARCHAR(255);
ALTER TABLE users ADD CONSTRAINT fk_user_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates (id);

