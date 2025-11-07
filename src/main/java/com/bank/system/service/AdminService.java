package com.bank.system.service;

import com.bank.system.dto.AccountDto;
import com.bank.system.dto.AdminDashboardStatsDto;
import com.bank.system.dto.TransactionDto;
import com.bank.system.dto.UserDto;
import com.bank.system.model.entity.Account;
import com.bank.system.model.entity.Transaction;
import com.bank.system.model.entity.User;
import com.bank.system.model.enums.AccountStatus;
import com.bank.system.model.enums.Role;
import com.bank.system.repository.AccountRepository;
import com.bank.system.repository.TransactionRepository;
import com.bank.system.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
    private EmailService emailService;

	@Autowired
	private AccountService accountService;

//    public List<AccountDto> getPendingAccounts() {
//        return accountRepository.findByStatus(AccountStatus.PENDING)
//                .stream()
//                .map(accountService::convertToDto) // Use the existing converter
//                .collect(Collectors.toList());
//    }
	public Page<AccountDto> getPendingAccounts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Account> accountPage = accountRepository.findByStatus(AccountStatus.PENDING, pageable);
		return accountPage.map(accountService::convertToDto);
	}
	
	@Transactional
    public AccountDto approveAccount(Long accountId) {
        Account account = findProcessableAccount(accountId);
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        
        account.setStatus(AccountStatus.APPROVED);
        account.setApprovedAt(LocalDateTime.now());
        account.setApprovedBy(adminUsername);
        account.setAdminRemarks("Account approved.");

        Account updatedAccount = accountRepository.save(account);
        User userToEmail = updatedAccount.getUser();
//        emailService.sendAccountApprovalEmail(updatedAccount.getUser(), updatedAccount);
        emailService.sendAccountApprovalEmail(userToEmail, updatedAccount);

        return accountService.convertToDto(updatedAccount);
    }

	@Transactional
	public AccountDto rejectAccount(Long accountId, String reason) {
		Account account = findProcessableAccount(accountId);

		String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();

		account.setStatus(AccountStatus.REJECTED);
		account.setApprovedBy(adminUsername);
		account.setAdminRemarks(reason);

		Account updatedAccount = accountRepository.save(account);
		return accountService.convertToDto(updatedAccount);
	}

	public AdminDashboardStatsDto getDashboardStats() {
		long total = accountRepository.count();
		long pending = accountRepository.countByStatus(AccountStatus.PENDING);
		long approved = accountRepository.countByStatus(AccountStatus.APPROVED);
		long rejected = accountRepository.countByStatus(AccountStatus.REJECTED);
		return new AdminDashboardStatsDto(total, pending, approved, rejected);
	}

	private Account findProcessableAccount(Long accountId) {
		Account account = accountRepository.findById(accountId)
				.orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + accountId));

		if (account.getStatus() != AccountStatus.PENDING) {
			throw new IllegalStateException(
					"Account has already been processed. Current status: " + account.getStatus());
		}
		return account;
	}

	@Autowired
	private UserRepository userRepository;

//    public List<User> searchCustomers(String query) {
//        if (query == null || query.isBlank()) {
//            return userRepository.findByRole(Role.CUSTOMER);
//        }
//        return userRepository.findByRoleAndUsernameContainingIgnoreCase(Role.CUSTOMER, query);
//    }
	public Page<UserDto> searchCustomers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        if (query == null || query.isBlank()) {
            userPage = userRepository.findByRole(Role.CUSTOMER, pageable);
        } else {
            userPage = userRepository.findByRoleAndUsernameContainingIgnoreCase(Role.CUSTOMER, query, pageable);
        }
        return userPage.map(this::convertUserToDto);
    }
	
	private UserDto convertUserToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        return userDto;
    }
	
//	public List<TransactionDto> getAccountHistory(String accountNumber) {
//	    // No ownership validation is needed because this is an admin-only function
//	    Account account = accountRepository.findByAccountNumber(accountNumber)
//	            .orElseThrow(() -> new IllegalArgumentException("Account not found."));
//
//	    // We can reuse the public method from TransactionService if it's refactored,
//	    // or call the repository directly. Let's call the repository for simplicity.
//	    return transactionRepository.findByAccountId(account.getId())
//	            .stream()
//	            .map(transactionService::convertToDto) // Reuse the converter
//	            .collect(Collectors.toList());
//	}
	
	public Page<TransactionDto> getAccountHistory(String accountNumber, int page, int size) {
	    Account account = accountRepository.findByAccountNumber(accountNumber)
	            .orElseThrow(() -> new IllegalArgumentException("Account not found."));
	            
	    Pageable pageable = PageRequest.of(page, size);
	    
	    Page<Transaction> transactionPage = transactionRepository.findByAccountIdWithPagination(account.getId(), pageable);
	    
	    return transactionPage.map(transactionService::convertToDto);
	}
	
	
}