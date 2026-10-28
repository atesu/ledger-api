
package com.ledger.ledger_api.transaction;

import com.ledger.ledger_api.account.Account;
import com.ledger.ledger_api.account.AccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository transactionRepository;

    public TransactionService(
            AccountRepository accountRepository,
            LedgerTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public UUID transfer(CreateTransactionRequest request) {
        UUID sourceId = request.sourceAccountId();
        UUID destinationId = request.destinationAccountId();
        BigDecimal amount = request.amount();

        if (sourceId.equals(destinationId)) {
            throw new IllegalArgumentException(
                    "Source and destination accounts must be different");
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        UUID firstId = sourceId.compareTo(destinationId) < 0
                ? sourceId : destinationId;
        UUID secondId = sourceId.compareTo(destinationId) < 0
                ? destinationId : sourceId;

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found: " + firstId));

        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found: " + secondId));

        Account source = sourceId.equals(firstId) ? first : second;
        Account destination = destinationId.equals(firstId) ? first : second;

        if (source.getCurrency() != destination.getCurrency()) {
            throw new IllegalArgumentException(
                    "Accounts must have the same currency");
        }

        int scale = source.getCurrency().getScale();

        if (amount.scale() > scale) {
            throw new IllegalArgumentException(
                    "Invalid decimal precision for currency");
        }

        BigDecimal newBalance = source.getBalance().subtract(amount);
        BigDecimal overdraftLimit = source.getOverdraftLimit();

        if (overdraftLimit != null
                && newBalance.compareTo(overdraftLimit.negate()) < 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds or overdraft limit exceeded");
        }

        source.debit(amount);
        destination.credit(amount);

        LedgerTransaction transaction =
                new LedgerTransaction("Account transfer");

        transaction.getEntries().add(
                new LedgerEntry(
                        transaction, source, EntryDirection.DEBIT, amount));

        transaction.getEntries().add(
                new LedgerEntry(
                        transaction, destination, EntryDirection.CREDIT, amount));

        LedgerTransaction saved = transactionRepository.save(transaction);

        return saved.getId();
    }
}
