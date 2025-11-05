package com.bank.system.service;

import com.bank.system.dto.AuthResponse;
import com.bank.system.dto.LoginRequest;
import com.bank.system.dto.RegisterRequest;
import com.bank.system.model.entity.User;
import com.bank.system.model.enums.Role;
import com.bank.system.repository.UserRepository;
import com.bank.system.security.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

//    public void register(RegisterRequest registerRequest) {
//        if (userRepository.existsByUsername(registerRequest.getUsername())) {
//            throw new IllegalStateException("Username is already taken");
//        }
//        if (userRepository.existsByEmail(registerRequest.getEmail())) {
//            throw new IllegalStateException("Email is already in use");
//        }
//
//        User user = new User();
//        user.setUsername(registerRequest.getUsername());
//        user.setEmail(registerRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
//        user.setFullName(registerRequest.getFullName());
//        user.setRole(registerRequest.getRole());
//        user.setActive(true); // Or false if you want an activation step
//
//        userRepository.save(user);
//    }
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    
    public void register(RegisterRequest registerRequest) {
        log.info("Attempting to register new user with username: {}", registerRequest.getUsername());

        log.info("Step 1: Checking if username exists...");
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.error("Registration failed: Username '{}' is already taken", registerRequest.getUsername());
            throw new IllegalStateException("Username is already taken");
        }
        log.info("Step 1 PASSED: Username is available.");

        log.info("Step 2: Checking if email exists...");
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.error("Registration failed: Email '{}' is already in use", registerRequest.getEmail());
            throw new IllegalStateException("Email is already in use");
        }
        log.info("Step 2 PASSED: Email is available.");

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        
        log.info("Step 3: Encoding password...");
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        log.info("Step 3 PASSED: Password encoded.");

        user.setFullName(registerRequest.getFullName());
//        user.setRole(registerRequest.getRole());
        if (registerRequest.getRole() == null) {
            user.setRole(Role.CUSTOMER); // Default to CUSTOMER for public registration
        } else {
            user.setRole(registerRequest.getRole()); // Use role for admin registration
        }
        user.setActive(true);

        log.info("Step 4: Saving user to the database...");
        userRepository.save(user);
        log.info("Step 4 PASSED: User saved successfully!");
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);
        
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return new AuthResponse(token, userDetails.getUsername(), role);
    }
}