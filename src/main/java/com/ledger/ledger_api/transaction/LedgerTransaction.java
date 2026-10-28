package com.ledger.ledger_api.transaction;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "ledger_transactions")
public class LedgerTransaction {

    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36, nullable = false)
    private UUID id;
    
    @Column(nullable = false, length = 255)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reverses_transaction_id", unique = true)
    private LedgerTransaction reverses;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "idempotency_key", unique = true, length = 36)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID idempotencyKey;
    
    @Column(name = "source_account_id", length = 36)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID sourceAccountId;

    @Column(name = "destination_account_id", length = 36)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID destinationAccountId;

    @Column(name = "request_amount", precision = 19, scale = 2)
    private BigDecimal requestAmount;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<LedgerEntry> entries = new ArrayList<>();

    public String getDescription() {
        return description;
    }

    public LedgerTransaction() {
        this.createdAt = Instant.now();
    }
    public LedgerTransaction(String description) {
        this.description = description;
        this.createdAt = Instant.now();
    }
    public LedgerTransaction(String description, LedgerTransaction reverses) {
        this.description = description;
        this.reverses = reverses;
        this.createdAt = Instant.now();
    }
    public LedgerTransaction(String description, UUID idempotencyKey) {
        this.description = description;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = Instant.now();
    }
    public LedgerTransaction(
            String description,
            UUID idempotencyKey,
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal requestAmount) {
        this.description = description;
        this.idempotencyKey = idempotencyKey;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.requestAmount = requestAmount;
        this.createdAt = Instant.now();
    }
    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }
    public List<LedgerEntry> getEntries() {
        return entries;
    }
    public LedgerTransaction getReverses() {
        return reverses;
    }
    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getRequestAmount() {
        return requestAmount;
    }
}