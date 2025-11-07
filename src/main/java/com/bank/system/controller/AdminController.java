package com.bank.system.controller;

import com.bank.system.dto.AccountDto;
import com.bank.system.dto.AdminActionRequest;
import com.bank.system.dto.AdminDashboardStatsDto;
import com.bank.system.dto.RegisterRequest;
import com.bank.system.dto.TransactionDto;
import com.bank.system.dto.UserDto;
import com.bank.system.dto.UserDto;
import com.bank.system.model.entity.User;
import com.bank.system.model.enums.Role;
import com.bank.system.service.AccountService;
import com.bank.system.service.AdminService;
import com.bank.system.service.AuthService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired 
    private AccountService accountService;
    
    @Autowired
    private AuthService authService;


//    @GetMapping("/accounts/pending")
//    public ResponseEntity<List<AccountDto>> getPendingAccounts() {
//        return ResponseEntity.ok(adminService.getPendingAccounts());
//    }
    
    @GetMapping("/accounts/pending")
    public ResponseEntity<Page<AccountDto>> getPendingAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getPendingAccounts(page, size));
    }

    @PutMapping("/accounts/approve/{accountId}")
    public ResponseEntity<?> approveAccount(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(adminService.approveAccount(accountId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/accounts/reject/{accountId}")
    public ResponseEntity<?> rejectAccount(@PathVariable Long accountId, @Valid @RequestBody AdminActionRequest request) {
        try {
            return ResponseEntity.ok(adminService.rejectAccount(accountId, request.getRemarks()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
    
//    @GetMapping("/customers")
//    public ResponseEntity<List<User>> searchCustomers(@RequestParam(required = false) String query) {
//        return ResponseEntity.ok(adminService.searchCustomers(query));
//    }
    
//    @GetMapping("/customers")
//    public ResponseEntity<Page<User>> searchCustomers(
//            @RequestParam(required = false) String query,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//        return ResponseEntity.ok(adminService.searchCustomers(query, page, size));
//    }
    
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    		
    @GetMapping("/customers")
    public ResponseEntity<Page<UserDto>> searchCustomers(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
    	log.info("Admin requested to search customers with query: '{}', page: {}", query, page);
        return ResponseEntity.ok(adminService.searchCustomers(query, page, size));
    }


    @GetMapping("/customers/{userId}/accounts")
    public ResponseEntity<List<AccountDto>> getCustomerAccounts(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }
    
//    @GetMapping("/accounts/history/{accountNumber}")
//    public ResponseEntity<?> getAccountHistoryForCustomer(@PathVariable String accountNumber) {
//        try {
//            return ResponseEntity.ok(adminService.getAccountHistory(accountNumber));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
    
    @GetMapping("/accounts/history/{accountNumber}")
    public ResponseEntity<Page<TransactionDto>> getAccountHistoryForCustomer(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // No try-catch needed; exceptions will be handled globally
        return ResponseEntity.ok(adminService.getAccountHistory(accountNumber, page, size));
    }
    
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            registerRequest.setRole(Role.ADMIN);
            authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Admin user registered successfully!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}