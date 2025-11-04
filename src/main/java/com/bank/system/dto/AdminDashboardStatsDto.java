package com.bank.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardStatsDto {
    private long totalAccounts;
    private long pendingAccounts;
    private long approvedAccounts;
    private long rejectedAccounts;
}