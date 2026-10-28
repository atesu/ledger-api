package com.ledger.ledger_api;

import com.ledger.ledger_api.messaging.TransactionEventPublisher;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.ledger.ledger_api.account.Account;
import com.ledger.ledger_api.account.AccountCurrency;
import com.ledger.ledger_api.account.AccountRepository;
import com.ledger.ledger_api.transaction.CreateTransactionRequest;
import com.ledger.ledger_api.transaction.EntryDirection;
import com.ledger.ledger_api.transaction.LedgerEntry;
import com.ledger.ledger_api.transaction.LedgerTransaction;
import com.ledger.ledger_api.transaction.LedgerTransactionRepository;
import com.ledger.ledger_api.transaction.TransactionService;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
@ExtendWith(MockitoExtension.class)
class LedgerTransactionTest {
	
	@Mock
	private AccountRepository accountRepository;

	@Mock
	private LedgerTransactionRepository transactionRepository;

	@InjectMocks
	private TransactionService transactionService;
	@Mock
	private TransactionEventPublisher eventPublisher;
    @Test
    void transactionShouldContainBalancedDebitAndCreditEntries() {
    	Account source = new Account(
    	        AccountCurrency.JPY,
    	        new BigDecimal("1000"));

    	Account destination = new Account(
    	        AccountCurrency.JPY,
    	        new BigDecimal("1000"));

        LedgerTransaction transaction =
                new LedgerTransaction("Test transfer");

        LedgerEntry debit = new LedgerEntry(
                transaction, source, EntryDirection.DEBIT,
                new BigDecimal("10.00"));

        LedgerEntry credit = new LedgerEntry(
                transaction, destination, EntryDirection.CREDIT,
                new BigDecimal("10.00"));

        transaction.getEntries().add(debit);
        transaction.getEntries().add(credit);

        assertEquals(2, transaction.getEntries().size());
        assertEquals(
                0,
                debit.getAmount().compareTo(credit.getAmount()));
        assertNotNull(transaction.getCreatedAt());
    }
    @Test
    void transferShouldReturnTransactionId() {
        assertNotNull(transactionService);
    }
    
    @Test
    void transferShouldDebitSourceAndCreditDestination() {
    	 UUID sourceId = UUID.randomUUID();
    	    UUID destinationId = UUID.randomUUID();
    	    UUID idempotencyKey = UUID.randomUUID();
    	Account source = new Account(
    	        AccountCurrency.JPY, new BigDecimal("1000"));
    	Account destination = new Account(
    	        AccountCurrency.JPY, new BigDecimal("1000"));

    	when(accountRepository.findByIdForUpdate(sourceId))
    	        .thenReturn(Optional.of(source));
    	when(accountRepository.findByIdForUpdate(destinationId))
    	        .thenReturn(Optional.of(destination));

    	CreateTransactionRequest request = new CreateTransactionRequest(
    	        sourceId, destinationId, new BigDecimal("10"), idempotencyKey);
    	
    	when(transactionRepository.findByIdempotencyKey(idempotencyKey))
        .thenReturn(Optional.empty());
    	
    	when(transactionRepository.save(Mockito.any(LedgerTransaction.class)))
        .thenAnswer(invocation -> {
            LedgerTransaction transaction = invocation.getArgument(0);
            return transaction;
        });
    	
    	UUID transactionId = transactionService.transfer(request);

   
    	assertEquals(0, source.getBalance().compareTo(new BigDecimal("-10")));
    	assertEquals(0, destination.getBalance().compareTo(new BigDecimal("10")));

    	Mockito.verify(transactionRepository).save(Mockito.any(LedgerTransaction.class));
    }
    @Test
    void repeatedRequestShouldReturnSameTransactionId() {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();

        Account source = new Account(
                AccountCurrency.JPY, new BigDecimal("1000"));
        Account destination = new Account(
                AccountCurrency.JPY, new BigDecimal("1000"));

        when(transactionRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.empty());

        when(accountRepository.findByIdForUpdate(sourceId))
                .thenReturn(Optional.of(source));
        when(accountRepository.findByIdForUpdate(destinationId))
                .thenReturn(Optional.of(destination));

        when(transactionRepository.save(Mockito.any(LedgerTransaction.class)))
                .thenAnswer(invocation -> {
                    LedgerTransaction transaction = invocation.getArgument(0);
                    return transaction;
                });

        CreateTransactionRequest request = new CreateTransactionRequest(
                sourceId, destinationId, new BigDecimal("10"), idempotencyKey);

        UUID firstId = transactionService.transfer(request);

        when(transactionRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.of(
                        new LedgerTransaction("Existing transfer",
                                idempotencyKey, sourceId, destinationId,
                                new BigDecimal("10"))));

        UUID secondId = transactionService.transfer(request);

        assertEquals(firstId, secondId);
        assertEquals(0, source.getBalance().compareTo(new BigDecimal("-10")));
        assertEquals(0, destination.getBalance().compareTo(new BigDecimal("10")));

        Mockito.verify(transactionRepository, Mockito.times(1))
                .save(Mockito.any(LedgerTransaction.class));
    }
    @Test
    void reusedIdempotencyKeyWithDifferentAmountShouldFail() {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();

        LedgerTransaction existing = new LedgerTransaction(
                "Existing transfer",
                idempotencyKey,
                sourceId,
                destinationId,
                new BigDecimal("10"));

        when(transactionRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.of(existing));

        CreateTransactionRequest request = new CreateTransactionRequest(
                sourceId,
                destinationId,
                new BigDecimal("20"),
                idempotencyKey);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.transfer(request));

        Mockito.verify(transactionRepository, Mockito.never())
                .save(Mockito.any(LedgerTransaction.class));
    }
    @Test
    void transferExceedingOverdraftLimitShouldFail() {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();

        Account source = new Account(
                AccountCurrency.JPY, new BigDecimal("5"));
        Account destination = new Account(
                AccountCurrency.JPY, new BigDecimal("1000"));

        when(transactionRepository.findByIdempotencyKey(idempotencyKey))
                .thenReturn(Optional.empty());

        when(accountRepository.findByIdForUpdate(sourceId))
                .thenReturn(Optional.of(source));
        when(accountRepository.findByIdForUpdate(destinationId))
                .thenReturn(Optional.of(destination));

        CreateTransactionRequest request = new CreateTransactionRequest(
                sourceId, destinationId, new BigDecimal("10"), idempotencyKey);

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.transfer(request));

        assertEquals(0, source.getBalance().compareTo(BigDecimal.ZERO));
        assertEquals(0, destination.getBalance().compareTo(BigDecimal.ZERO));

        Mockito.verify(transactionRepository, Mockito.never())
                .save(Mockito.any(LedgerTransaction.class));
    }
}
