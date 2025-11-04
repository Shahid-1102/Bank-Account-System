package com.bank.system.dto;

import lombok.Data;

@Data
public class StatementRequestDto {
    private String accountNumber;
    private String startDate;
    private String endDate;
}