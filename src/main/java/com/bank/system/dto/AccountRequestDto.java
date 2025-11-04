package com.bank.system.dto;

import com.bank.system.model.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AccountRequestDto {

    @NotNull(message = "Account type must be specified")
    private AccountType accountType;

    @NotNull(message = "Initial deposit cannot be null")
    @DecimalMin(value = "1000.00", message = "Minimum initial deposit must be at least 1000")
    private BigDecimal initialDeposit;
}