package com.bank.system.dto;

import com.bank.system.model.enums.AccountStatus;
import com.bank.system.model.enums.AccountType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDto {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
    private Long userId;
    private String userFullName;
    private LocalDateTime createdAt;
    private String adminRemarks;
}