
CREATE TABLE accounts (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    currency ENUM('EUR', 'USD', 'JPY') NOT NULL,
    overdraft_limit DECIMAL(19,2) NOT NULL,
    balance DECIMAL(19,2) NOT NULL,
    version BIGINT
);

CREATE TABLE ledger_transactions (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    reverses_transaction_id VARCHAR(36) NULL UNIQUE,
    created_at DATETIME(6) NOT NULL,
    idempotency_key VARCHAR(36) NULL UNIQUE,
    source_account_id VARCHAR(36) NULL,
    destination_account_id VARCHAR(36) NULL,
    request_amount DECIMAL(19,2) NULL,
    CONSTRAINT fk_transaction_reverses
        FOREIGN KEY (reverses_transaction_id)
        REFERENCES ledger_transactions(id)
);

CREATE TABLE ledger_entries (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    transaction_id VARCHAR(36) NOT NULL,
    account_id VARCHAR(36) NOT NULL,
    direction VARCHAR(10) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    CONSTRAINT fk_entry_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES ledger_transactions(id),
    CONSTRAINT fk_entry_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id)
);

CREATE INDEX idx_ledger_entries_account_id
    ON ledger_entries(account_id);

CREATE INDEX idx_ledger_entries_transaction_id
    ON ledger_entries(transaction_id);
