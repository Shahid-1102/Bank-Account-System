package com.bank.system.controller;

import com.bank.system.dto.AccountDto;
import com.bank.system.dto.AccountRequestDto;
import com.bank.system.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequestDto accountRequestDto) {
        try {
            AccountDto newAccount = accountService.createAccount(accountRequestDto);
            return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/my-accounts")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<AccountDto>> getMyAccounts() {
        List<AccountDto> accounts = accountService.getAccountsForCurrentUser();
        return ResponseEntity.ok(accounts);
    }
}