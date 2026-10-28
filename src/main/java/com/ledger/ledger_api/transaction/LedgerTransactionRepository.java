package com.ledger.ledger_api.transaction;



import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LedgerTransactionRepository
        extends JpaRepository<LedgerTransaction, UUID> {
	boolean existsByReverses(LedgerTransaction reverses);
}