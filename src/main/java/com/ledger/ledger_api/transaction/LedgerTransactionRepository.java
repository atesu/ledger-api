package com.ledger.ledger_api.transaction;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LedgerTransactionRepository
        extends JpaRepository<LedgerTransaction, UUID> {
	boolean existsByReverses(LedgerTransaction reverses);
	Optional<LedgerTransaction> findByIdempotencyKey(UUID idempotencyKey);
	@EntityGraph(attributePaths = {
		    "entries",
		    "entries.account",
		    "reverses"
		})
		Optional<LedgerTransaction> findWithEntriesById(UUID id);
}