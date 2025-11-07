package com.bank.system.service;

import com.bank.system.model.entity.Account;
import com.bank.system.model.entity.Transaction;
import com.bank.system.repository.AccountRepository;
import com.bank.system.repository.TransactionRepository;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ReportService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TemplateEngine templateEngine;

    public ByteArrayInputStream generatePdfStatement(String accountNumber, LocalDate startDate, LocalDate endDate) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        validateAccountOwnership(account);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionRepository.findByAccountIdAndTimestampBetween(account.getId(), startDateTime, endDateTime);

        Context context = new Context();
        context.setVariable("account", account);
        context.setVariable("transactions", transactions);
        context.setVariable("startDate", startDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        context.setVariable("endDate", endDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        context.setVariable("generationDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        String htmlContent = templateEngine.process("statement", context);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, out);

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void validateAccountOwnership(Account account) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!account.getUser().getUsername().equals(currentUsername)) {
            throw new SecurityException("Access Denied: You do not own this account.");
        }
    }
}