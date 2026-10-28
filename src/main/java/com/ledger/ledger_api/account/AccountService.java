package com.ledger.ledger_api.account;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class AccountService {
	private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        validate(request);

        Account account = new Account(
                request.currency(),
                request.overdraftLimit()
        );

        Account saved = accountRepository.save(account);
        return AccountResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public AccountResponse getById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(id));

        return AccountResponse.from(account);
    }

    private void validate(CreateAccountRequest request) {
        if (request.currency() == null) {
            throw new IllegalArgumentException(
                    "Currency is required");
        }

        if (request.overdraftLimit() == null
                || request.overdraftLimit()
                        .compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Overdraft limit must be non-negative");
        }

        if (request.overdraftLimit().scale()
                > request.currency().getScale()) {
            throw new IllegalArgumentException(
                    "Too many decimal places for "
                            + request.currency());
        }
    }
}
