package com.bank.system.controller;

import com.bank.system.dto.TransactionDto;
import com.bank.system.dto.TransactionRequestDto;
import com.bank.system.dto.TransferRequestDto;
import com.bank.system.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody TransactionRequestDto request) {
        try {
            TransactionDto transaction = transactionService.deposit(request.getAccountNumber(), request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> withdraw(@Valid @RequestBody TransactionRequestDto request) {
        try {
            TransactionDto transaction = transactionService.withdraw(request.getAccountNumber(), request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequestDto request) {
        try {
            TransactionDto transaction = transactionService.transfer(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history/{accountNumber}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<?> getHistory(@PathVariable String accountNumber) {
        try {
            List<TransactionDto> history = transactionService.getTransactionHistory(accountNumber);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/mini-statement/{accountNumber}")
    public ResponseEntity<?> getMiniStatement(@PathVariable String accountNumber) {
        try {
            List<TransactionDto> history = transactionService.getMiniStatement(accountNumber);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}