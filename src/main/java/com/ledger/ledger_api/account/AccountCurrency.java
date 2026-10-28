package com.ledger.ledger_api.account;

public enum AccountCurrency {

    EUR(2),
    USD(2),
    JPY(0);

    private final int scale;

    AccountCurrency(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }
}