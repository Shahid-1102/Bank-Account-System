package com.bank.system.controller;

import com.bank.system.dto.StatementRequestDto;
import com.bank.system.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAuthority('CUSTOMER')")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @PostMapping("/download-statement")
    public ResponseEntity<?> downloadStatement(@RequestBody StatementRequestDto request) {
        
        log.info("Received request body for statement: {}", request);

        LocalDate parsedStartDate;
        LocalDate parsedEndDate;

        try {
            parsedStartDate = LocalDate.parse(request.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            parsedEndDate = LocalDate.parse(request.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse date strings from request body.", e);
            return ResponseEntity.badRequest().body("Invalid date format in request. Please use YYYY-MM-DD.");
        }

        try {
            ByteArrayInputStream pdf = reportService.generatePdfStatement(request.getAccountNumber(), parsedStartDate, parsedEndDate);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=statement-" + request.getAccountNumber() + ".pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdf));

        } catch (Exception e) {
            log.error("Error occurred during PDF generation.", e);
            return ResponseEntity.badRequest().body("Error generating statement: " + e.getMessage());
        }
    }
}