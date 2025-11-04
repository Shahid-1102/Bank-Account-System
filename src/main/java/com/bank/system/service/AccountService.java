package com.bank.system.service;

import com.bank.system.dto.AccountDto;
import com.bank.system.dto.AccountRequestDto;
import com.bank.system.model.entity.Account;
import com.bank.system.model.entity.User;
import com.bank.system.model.enums.AccountStatus;
import com.bank.system.repository.AccountRepository;
import com.bank.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AccountDto createAccount(AccountRequestDto accountRequestDto) {
        // 1. Get the currently logged-in user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Apply business rule: Max 3 accounts per user
        if (accountRepository.countByUserId(currentUser.getId()) >= 3) {
            throw new IllegalStateException("User cannot have more than 3 accounts.");
        }

        // 3. Create and populate the new Account entity
        Account newAccount = new Account();
        newAccount.setUser(currentUser);
        newAccount.setAccountType(accountRequestDto.getAccountType());
        newAccount.setBalance(accountRequestDto.getInitialDeposit());
        newAccount.setStatus(AccountStatus.PENDING); // Accounts require admin approval
        newAccount.setAccountNumber(generateUniqueAccountNumber());

        // 4. Save the account to the database
        Account savedAccount = accountRepository.save(newAccount);

        // 5. Convert to DTO and return
        return convertToDto(savedAccount);
    }

    public List<AccountDto> getAccountsForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        List<Account> accounts = accountRepository.findByUserId(currentUser.getId());
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // A simple method to generate a unique account number
    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            // Generate a 10-digit number
            accountNumber = String.format("%010d", random.nextLong(1_000_000_000L, 10_000_000_000L));
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }

    // Helper method to map an Entity to a DTO
    public AccountDto convertToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        dto.setUserId(account.getUser().getId());
        dto.setUserFullName(account.getUser().getFullName());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setAdminRemarks(account.getAdminRemarks());
        return dto;
    }
    
    public List<AccountDto> getAccountsByUserId(Long userId) {
        // No ownership validation here since it's for an admin
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}