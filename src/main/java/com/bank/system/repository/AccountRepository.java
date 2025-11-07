package com.bank.system.repository;

import com.bank.system.model.entity.Account;
import com.bank.system.model.entity.User;
import com.bank.system.model.enums.AccountStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long userId);

    long countByUserId(Long userId);
    
//    List<Account> findByStatus(AccountStatus status);
    Page<Account> findByStatus(AccountStatus status, Pageable pageable);

    
    long countByStatus(AccountStatus status);
    
    long countByUserAndStatusNot(User user, AccountStatus status);
}