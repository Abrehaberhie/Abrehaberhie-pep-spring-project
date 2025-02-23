package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

//public class AccountService {
//}

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // Registers a new account if the username is not taken.
    public Account register(Account account) {
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            return null; // Conflict: username exists
        }
        return accountRepository.save(account);
    }

    // Checks credentials for login.
    public Account login(Account account) {
        Account existing = accountRepository.findByUsername(account.getUsername());
        if (existing != null && existing.getPassword().equals(account.getPassword())) {
            return existing;
        }
        return null;
    }

    // Checks if an account exists by its ID.
    public boolean exists(Integer accountId) {
        if (accountId == null) return false;
        return accountRepository.existsById(accountId);
    }
}
