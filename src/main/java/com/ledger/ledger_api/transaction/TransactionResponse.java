package com.ledger.ledger_api.transaction;




import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        String description,
        Instant createdAt,
        UUID idempotencyKey,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal requestAmount,
        UUID reversesTransactionId,
        List<EntryResponse> entries
) {
    public record EntryResponse(
            UUID id,
            UUID accountId,
            String direction,
            BigDecimal amount
    ) {}
}
