package com.ledger.ledger_api.account;


import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        AccountCurrency currency,
        BigDecimal overdraftLimit,
        BigDecimal balance
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getCurrency(),
                account.getOverdraftLimit(),
                account.getBalance()
        );
    }
}

