package com.ledger.ledger_api.account;

import java.util.UUID;

public class AccountNotFoundException
        extends RuntimeException {

    public AccountNotFoundException(UUID id) {
        super("Account not found: " + id);
    }
}