package com.bank.system.dto;

import com.bank.system.model.enums.TransactionStatus;
import com.bank.system.model.enums.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {
    private Long id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String fromAccountNumber;
    private String toAccountNumber;
    private LocalDateTime timestamp;
    private TransactionStatus status;
    private String description;
    private BigDecimal balanceAfter;
}