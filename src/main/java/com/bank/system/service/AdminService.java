package com.bank.system.service;

import com.bank.system.dto.AccountDto;
import com.bank.system.dto.AdminDashboardStatsDto;
import com.bank.system.model.entity.Account;
import com.bank.system.model.enums.AccountStatus;
import com.bank.system.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    // We inject AccountService to reuse its public convertToDto method.
    // This is a common and acceptable pattern.
    @Autowired
    private AccountService accountService;

    // Method 1: Get Pending Accounts
    public List<AccountDto> getPendingAccounts() {
        return accountRepository.findByStatus(AccountStatus.PENDING)
                .stream()
                .map(accountService::convertToDto) // Use the existing converter
                .collect(Collectors.toList());
    }

    // Method 2: Approve an Account
    @Transactional
    public AccountDto approveAccount(Long accountId) {
        Account account = findProcessableAccount(accountId);
        
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        
        account.setStatus(AccountStatus.APPROVED);
        account.setApprovedAt(LocalDateTime.now());
        account.setApprovedBy(adminUsername);
        account.setAdminRemarks("Account approved.");

        Account updatedAccount = accountRepository.save(account);
        return accountService.convertToDto(updatedAccount);
    }

    // Method 3: Reject an Account
    @Transactional
    public AccountDto rejectAccount(Long accountId, String reason) {
        Account account = findProcessableAccount(accountId);

        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        account.setStatus(AccountStatus.REJECTED);
        account.setApprovedBy(adminUsername); // The admin who processed it
        account.setAdminRemarks(reason);

        Account updatedAccount = accountRepository.save(account);
        return accountService.convertToDto(updatedAccount);
    }

    // Method 4: Get Dashboard Statistics
    public AdminDashboardStatsDto getDashboardStats() {
        long total = accountRepository.count();
        long pending = accountRepository.countByStatus(AccountStatus.PENDING);
        long approved = accountRepository.countByStatus(AccountStatus.APPROVED);
        long rejected = accountRepository.countByStatus(AccountStatus.REJECTED);
        return new AdminDashboardStatsDto(total, pending, approved, rejected);
    }

    // A private helper method to find and validate an account before processing
    private Account findProcessableAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

        if (account.getStatus() != AccountStatus.PENDING) {
            throw new IllegalStateException("Account has already been processed. Current status: " + account.getStatus());
        }
        return account;
    }
}