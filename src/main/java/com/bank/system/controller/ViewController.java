package com.bank.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/auth/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/auth/register")
    public String registerPage() {
        return "register";
    }
    
    @GetMapping("/customer/dashboard")
    public String customerDashboard() {
        return "customer/dashboard";
    }

    @GetMapping("/customer/create-account")
    public String createAccountPage() {
        return "customer/create-account";
    }
    
    @GetMapping("/customer/history")
    public String historyPage() {
        return "customer/history";
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
    
    @GetMapping("/customer/statement")
    public String statementPage() {
        return "customer/statement";
    }
    
    @GetMapping("/admin/customer-details")
    public String customerDetailsPage() {
        return "admin/customer-details";
    }
    
    @GetMapping("/admin/history")
    public String adminHistoryPage() {
        return "admin/history";
    }
}