
package com.ledger.ledger_api.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

	@Id
	@GeneratedValue
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(length = 36, nullable = false)
	private UUID id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private AccountCurrency currency;

    @Column(
        name = "overdraft_limit",
        nullable = true,
        precision = 19,
        scale = 2
    )
    private BigDecimal overdraftLimit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Version
    private Long version;

    protected Account() {
        // Required by JPA
    }

    public Account(
            AccountCurrency currency,
            BigDecimal overdraftLimit) {
        this.currency = currency;
        this.overdraftLimit = overdraftLimit;
        this.balance = BigDecimal.ZERO;
    }

    public UUID getId() {
        return id;
    }

    public AccountCurrency getCurrency() {
        return currency;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }
    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
