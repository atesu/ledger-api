package com.ledger.ledger_api.transaction;


import jakarta.persistence.*;
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

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<LedgerEntry> getEntries() {
        return entries;
    }
    public LedgerTransaction getReverses() {
        return reverses;
    }
}