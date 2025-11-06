package com.bank.system.dto;

import com.bank.system.model.enums.Role;
import lombok.Data;

@Data
public class ProfileDto {
    private String username;
    private String fullName;
    private String email;
    private Role role;
}