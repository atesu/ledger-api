package com.ledger.ledger_api.account;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotNull AccountCurrency currency,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        @Digits(integer = 17, fraction = 2)
        BigDecimal overdraftLimit
) {
}

