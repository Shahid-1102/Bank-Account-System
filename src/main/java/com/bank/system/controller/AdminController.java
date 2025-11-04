package com.bank.system.controller;

import com.bank.system.dto.AccountDto;
import com.bank.system.dto.AdminActionRequest;
import com.bank.system.dto.AdminDashboardStatsDto;
import com.bank.system.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/accounts/pending")
    public ResponseEntity<List<AccountDto>> getPendingAccounts() {
        return ResponseEntity.ok(adminService.getPendingAccounts());
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
}