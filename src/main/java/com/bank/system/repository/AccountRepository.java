package com.bank.system.repository;

import com.bank.system.model.entity.Account;
import com.bank.system.model.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds a single account by its unique account number.
     * @param accountNumber The account number.
     * @return An Optional containing the Account if found.
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Finds all accounts associated with a specific user ID.
     * @param userId The ID of the user.
     * @return A list of accounts belonging to the user.
     */
    List<Account> findByUserId(Long userId);

    /**
     * Counts the number of accounts a user has. Useful for validation rules.
     * @param userId The ID of the user.
     * @return The total number of accounts for the user.
     */
    long countByUserId(Long userId);
    
    /**
     * Finds all accounts with a given status. Essential for the admin approval module.
     * @param status The status to filter by (e.g., PENDING).
     * @return A list of accounts matching the status.
     */
    List<Account> findByStatus(AccountStatus status);
    
    long countByStatus(AccountStatus status);
}