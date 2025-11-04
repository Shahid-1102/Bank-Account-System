package com.bank.system.repository;

import com.bank.system.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions related to a specific account (either as sender or receiver),
     * ordered by the most recent first.
     * @param accountId The ID of the account.
     * @return A list of transactions.
     */
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
    
    /**
     * Finds transactions for a specific account within a date range.
     * @param accountId The ID of the account.
     * @param startDate The start of the date range.
     * @param endDate The end of the date range.
     * @return A list of transactions.
     */
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) AND t.timestamp BETWEEN :startDate AND :endDate ORDER BY t.timestamp DESC")
    List<Transaction> findByAccountIdAndTimestampBetween(
        @Param("accountId") Long accountId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate);
    
    List<Transaction> findFirst10ByFromAccountIdOrToAccountIdOrderByIdDesc(Long fromId, Long toId);
}