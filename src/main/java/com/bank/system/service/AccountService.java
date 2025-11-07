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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

//        if (accountRepository.countByUserId(currentUser.getId()) >= 3) {
//            throw new IllegalStateException("User cannot have more than 3 accounts.");
//        }
        if (accountRepository.countByUserAndStatusNot(currentUser, AccountStatus.REJECTED) >= 3) {
            throw new IllegalStateException("Account limit reached. A user cannot have more than 3 active or pending accounts.");
        }

        Account newAccount = new Account();
        newAccount.setUser(currentUser);
        newAccount.setAccountType(accountRequestDto.getAccountType());
        newAccount.setBalance(accountRequestDto.getInitialDeposit());
        newAccount.setStatus(AccountStatus.PENDING);
        newAccount.setAccountNumber(generateUniqueAccountNumber());

        Account savedAccount = accountRepository.save(newAccount);

        return convertToDto(savedAccount);
    }

    public List<AccountDto> getAccountsForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        List<Account> accounts = accountRepository.findByUserId(currentUser.getId());
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.format("%010d", random.nextLong(1_000_000_000L, 10_000_000_000L));
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }

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
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}