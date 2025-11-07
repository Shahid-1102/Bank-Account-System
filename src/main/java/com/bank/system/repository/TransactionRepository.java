package com.bank.system.repository;

import com.bank.system.model.entity.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) AND t.timestamp BETWEEN :startDate AND :endDate ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountIdAndTimestampBetween(
        @Param("accountId") Long accountId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate);
    
    List<Transaction> findFirst10ByFromAccountIdOrToAccountIdOrderByIdDesc(Long fromId, Long toId);
    
    @Query(value = "SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.timestamp DESC",
    	       countQuery = "SELECT count(t) FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId")
    	Page<Transaction> findByAccountIdWithPagination(@Param("accountId") Long accountId, Pageable pageable);
}