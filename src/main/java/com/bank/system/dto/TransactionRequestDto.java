package com.bank.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequestDto {
    @NotBlank(message = "Account number cannot be blank")
    private String accountNumber;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "100.00", message = "Minimum transaction amount is 100")
    private BigDecimal amount;
}