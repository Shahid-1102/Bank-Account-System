package com.bank.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminActionRequest {
    @NotBlank(message = "Remarks or rejection reason cannot be blank")
    private String remarks;
}