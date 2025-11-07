package com.bank.system.service;

import com.bank.system.model.entity.Account;
import com.bank.system.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendAccountApprovalEmail(User user, Account account) {
        String to = user.getEmail();
        String subject = "Your Bank Account has been Approved!";

        String body = String.format(
            "Dear %s,\n\n" +
            "Congratulations! Your application for a new %s account has been approved.\n\n" +
            "Your new account number is: %s\n\n" +
            "You can now log in to your dashboard and start performing transactions.\n\n" +
            "Thank you for choosing Bank of Manhattan.\n\n" +
            "Sincerely,\nThe Bank of Manhattan Team",
            user.getFullName(),
            account.getAccountType(),
            account.getAccountNumber()
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}