package com.bank.system.service;

import com.bank.system.dto.TransactionDto;
import com.bank.system.dto.TransferRequestDto;
import com.bank.system.model.entity.Account;
import com.bank.system.model.entity.Transaction;
import com.bank.system.model.entity.User;
import com.bank.system.model.enums.AccountStatus;
import com.bank.system.model.enums.AccountType;
import com.bank.system.model.enums.TransactionStatus;
import com.bank.system.model.enums.TransactionType;
import com.bank.system.repository.AccountRepository;
import com.bank.system.repository.TransactionRepository;
import com.bank.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final BigDecimal MINIMUM_SAVINGS_BALANCE = new BigDecimal("500.00");

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TransactionDto deposit(String accountNumber, BigDecimal amount) {
        Account account = findAndValidateAccount(accountNumber);
        
        account.setBalance(account.getBalance().add(amount));
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = createTransaction(null, savedAccount, amount, TransactionType.DEPOSIT, "Deposit to account");
        return convertToDto(transaction);
    }

    @Transactional
    public TransactionDto withdraw(String accountNumber, BigDecimal amount) {
        Account account = findAndValidateAccount(accountNumber);
        
        // Security check: ensure the account belongs to the logged-in user
        validateAccountOwnership(account);

        // Business rule: Check for sufficient funds
        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds for withdrawal.");
        }

        // Business rule: Maintain minimum balance for savings accounts
        if (account.getAccountType() == AccountType.SAVINGS && newBalance.compareTo(MINIMUM_SAVINGS_BALANCE) < 0) {
            throw new IllegalStateException("Withdrawal would bring balance below the minimum of " + MINIMUM_SAVINGS_BALANCE);
        }

        account.setBalance(newBalance);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = createTransaction(savedAccount, null, amount, TransactionType.WITHDRAWAL, "Withdrawal from account");
        return convertToDto(transaction);
    }

    @Transactional
    public TransactionDto transfer(TransferRequestDto transferRequest) {
        if (transferRequest.getFromAccountNumber().equals(transferRequest.getToAccountNumber())) {
            throw new IllegalArgumentException("Source and destination accounts cannot be the same.");
        }

        Account fromAccount = findAndValidateAccount(transferRequest.getFromAccountNumber());
        Account toAccount = findAndValidateAccount(transferRequest.getToAccountNumber());
        BigDecimal amount = transferRequest.getAmount();

        // Security check: ensure the source account belongs to the logged-in user
        validateAccountOwnership(fromAccount);

        // Business rule: Check for sufficient funds in source account
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
        if (newFromBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds for transfer.");
        }
        if (fromAccount.getAccountType() == AccountType.SAVINGS && newFromBalance.compareTo(MINIMUM_SAVINGS_BALANCE) < 0) {
            throw new IllegalStateException("Transfer would bring source balance below the minimum of " + MINIMUM_SAVINGS_BALANCE);
        }

        // Update balances
        fromAccount.setBalance(newFromBalance);
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Save accounts
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        String description = String.format("Transfer from %s to %s", fromAccount.getAccountNumber(), toAccount.getAccountNumber());
        Transaction transaction = createTransaction(fromAccount, toAccount, amount, TransactionType.TRANSFER, description);
        return convertToDto(transaction);
    }
    
    public List<TransactionDto> getTransactionHistory(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));
        
        validateAccountOwnership(account);

        return transactionRepository.findByAccountId(account.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- Helper Methods ---
    
    private Account findAndValidateAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account with number " + accountNumber + " not found."));
        if (account.getStatus() != AccountStatus.APPROVED) {
            throw new IllegalStateException("Account is not active. Current status: " + account.getStatus());
        }
        return account;
    }

    private void validateAccountOwnership(Account account) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!account.getUser().getUsername().equals(currentUsername)) {
            throw new SecurityException("Access Denied: You do not own this account.");
        }
    }

    private Transaction createTransaction(Account from, Account to, BigDecimal amount, TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(description);
        // Determine which account's balance to record
        if (type == TransactionType.DEPOSIT) {
            transaction.setBalanceAfter(to.getBalance());
        } else {
            transaction.setBalanceAfter(from.getBalance());
        }
        return transactionRepository.save(transaction);
    }
    
    private TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setFromAccountNumber(transaction.getFromAccount() != null ? transaction.getFromAccount().getAccountNumber() : null);
        dto.setToAccountNumber(transaction.getToAccount() != null ? transaction.getToAccount().getAccountNumber() : null);
        dto.setTimestamp(transaction.getTimestamp());
        dto.setStatus(transaction.getStatus());
        dto.setDescription(transaction.getDescription());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        return dto;
    }
}