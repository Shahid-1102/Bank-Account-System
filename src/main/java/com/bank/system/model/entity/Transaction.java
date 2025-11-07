package com.bank.system.model.entity;

import com.bank.system.model.enums.TransactionStatus;
import com.bank.system.model.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account")
    private Account fromAccount;

    // Destination account (can be null for WITHDRAWAL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account")
    private Account toAccount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    
    private String description;
    
    @NotNull
    @Column(name = "balance_after")
    private BigDecimal balanceAfter;
}