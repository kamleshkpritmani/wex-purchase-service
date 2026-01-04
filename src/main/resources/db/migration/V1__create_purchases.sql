-- Main transaction table for the Wex Purchase Service
CREATE TABLE IF NOT EXISTS purchases (
  id VARCHAR(64) PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  description VARCHAR(50) NOT NULL,
  transaction_date DATE NOT NULL,
  purchase_amount_usd DECIMAL(19,2) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  created_by VARCHAR(128) NOT NULL,
  updated_by VARCHAR(128) NOT NULL
);

-- Index to optimize retrieval of user-specific purchase history
CREATE INDEX idx_purchases_username ON purchases(username);