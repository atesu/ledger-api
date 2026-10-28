package com.ledger.ledger_api.transaction;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, UUID> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {

        UUID transactionId = transactionService.transfer(request);

        return Map.of("transactionId", transactionId);
    }
}