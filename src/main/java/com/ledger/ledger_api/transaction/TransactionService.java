
package com.ledger.ledger_api.transaction;

import com.ledger.ledger_api.account.Account;
import com.ledger.ledger_api.account.AccountRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import java.math.BigDecimal;
import java.util.List;
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
        UUID idempotencyKey = request.idempotencyKey();
        
        LedgerTransaction existing =
        	    transactionRepository.findByIdempotencyKey(idempotencyKey)
        	        .orElse(null);

        	if (existing != null) {
        	    boolean sameRequest =
        	        sourceId.equals(existing.getSourceAccountId())
        	        && destinationId.equals(existing.getDestinationAccountId())
        	        && amount.compareTo(existing.getRequestAmount()) == 0;

        	    if (sameRequest) {
        	        return existing.getId();
        	    }

        	    throw new IllegalArgumentException(
        	        "Idempotency key has already been used for a different request");
        	}

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

        if (amount.stripTrailingZeros().scale() > scale) {
            throw new IllegalArgumentException("Invalid decimal precision for currency");
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

        LedgerTransaction transaction = new LedgerTransaction(
        	    "Account transfer",
        	    idempotencyKey,
        	    sourceId,
        	    destinationId,
        	    amount
        	);

        transaction.getEntries().add(
                new LedgerEntry(
                        transaction, source, EntryDirection.DEBIT, amount));

        transaction.getEntries().add(
                new LedgerEntry(
                        transaction, destination, EntryDirection.CREDIT, amount));

        LedgerTransaction saved = transactionRepository.save(transaction);

        return saved.getId();
    }

@Transactional
public UUID reverse(UUID transactionId) {
    // 1. Find the original transaction.
    LedgerTransaction original = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Transaction not found: " + transactionId));

    // 2. A reversal cannot itself be reversed.
    if (original.getReverses() != null) {
        throw new IllegalArgumentException(
            "A reversal transaction cannot be reversed");
    }

    // 3. Prevent a second reversal.
    if (transactionRepository.existsByReverses(original)) {
        throw new IllegalArgumentException(
            "Transaction has already been reversed");
    }

    // 4. Get the original entries and account IDs.
    java.util.List<LedgerEntry> entries = original.getEntries();

    if (entries.size() < 2) {
        throw new IllegalArgumentException(
            "Transaction must contain at least two entries");
    }

    java.util.List<UUID> accountIds = entries.stream()
        .map(entry -> entry.getAccount().getId())
        .distinct()
        .sorted()
        .toList();

    // 5. Lock accounts in a consistent order.
    java.util.Map<UUID, Account> lockedAccounts =
        new java.util.HashMap<>();

    for (UUID accountId : accountIds) {
        Account account = accountRepository.findByIdForUpdate(accountId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Account not found: " + accountId));

        lockedAccounts.put(accountId, account);
    }

    // 6. Calculate the resulting balances without changing accounts.
    java.util.Map<UUID, BigDecimal> resultingBalances =
        new java.util.HashMap<>();

    for (UUID accountId : accountIds) {
        resultingBalances.put(
            accountId,
            lockedAccounts.get(accountId).getBalance());
    }

    for (LedgerEntry entry : entries) {
        if (entry.getDirection() == null) {
            throw new IllegalArgumentException(
                "Cannot reverse a transaction with missing entry direction");
        }

        UUID accountId = entry.getAccount().getId();
        BigDecimal currentBalance = resultingBalances.get(accountId);
        BigDecimal amount = entry.getAmount();

        BigDecimal newBalance;

        if (entry.getDirection() == EntryDirection.DEBIT) {
            // Reversal of a debit credits the account.
            newBalance = currentBalance.add(amount);
        } else {
            // Reversal of a credit debits the account.
            newBalance = currentBalance.subtract(amount);
        }

        resultingBalances.put(accountId, newBalance);
    }

    // 7. Validate overdraft limits before changing any balance.
    for (UUID accountId : accountIds) {
        Account account = lockedAccounts.get(accountId);
        BigDecimal newBalance = resultingBalances.get(accountId);
        BigDecimal overdraftLimit = account.getOverdraftLimit();

        if (overdraftLimit != null
                && newBalance.compareTo(overdraftLimit.negate()) < 0) {
            throw new IllegalArgumentException(
                "Reversal would exceed overdraft limit for account: "
                    + accountId);
        }
    }

    // 8. Apply the validated balance changes.
    for (UUID accountId : accountIds) {
        Account account = lockedAccounts.get(accountId);

        BigDecimal difference = resultingBalances.get(accountId)
            .subtract(account.getBalance());

        if (difference.signum() > 0) {
            account.credit(difference);
        } else if (difference.signum() < 0) {
            account.debit(difference.negate());
        }
    }

    // 9. Create a new transaction with opposite entries.
    LedgerTransaction reversal = new LedgerTransaction(
        "Reversal of transaction " + transactionId,
        original);

    for (LedgerEntry entry : entries) {
        EntryDirection oppositeDirection =
            entry.getDirection() == EntryDirection.DEBIT
                ? EntryDirection.CREDIT
                : EntryDirection.DEBIT;

        reversal.getEntries().add(
            new LedgerEntry(
                reversal,
                lockedAccounts.get(entry.getAccount().getId()),
                oppositeDirection,
                entry.getAmount()));
    }

    // 10. Save the reversal in the same database transaction.
    LedgerTransaction saved = transactionRepository.save(reversal);

    return saved.getId();
}
@Transactional(readOnly = true)
public TransactionResponse getTransaction(UUID transactionId) {
    LedgerTransaction transaction = transactionRepository
            .findWithEntriesById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Transaction not found: " + transactionId));

    List<TransactionResponse.EntryResponse> entries =
            transaction.getEntries().stream()
                    .map(entry -> new TransactionResponse.EntryResponse(
                            entry.getId(),
                            entry.getAccount().getId(),
                            entry.getDirection().name(),
                            entry.getAmount()
                    ))
                    .toList();

    UUID reversesTransactionId = transaction.getReverses() == null
            ? null
            : transaction.getReverses().getId();

    return new TransactionResponse(
            transaction.getId(),
            transaction.getDescription(),
            transaction.getCreatedAt(),
            transaction.getIdempotencyKey(),
            transaction.getSourceAccountId(),
            transaction.getDestinationAccountId(),
            transaction.getRequestAmount(),
            reversesTransactionId,
            entries
    );
}
}
