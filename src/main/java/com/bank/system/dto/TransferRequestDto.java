package com.bank.system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequestDto {
    @NotBlank(message = "Source account number cannot be blank")
    private String fromAccountNumber;

    @NotBlank(message = "Destination account number cannot be blank")
    private String toAccountNumber;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "100.00", message = "Minimum transaction amount is 100")
    private BigDecimal amount;
}