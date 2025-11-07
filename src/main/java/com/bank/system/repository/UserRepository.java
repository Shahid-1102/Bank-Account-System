package com.bank.system.repository;

import com.bank.system.model.entity.User;
import com.bank.system.model.enums.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

//    List<User> findByRole(Role role);
//    
//    List<User> findByRoleAndUsernameContainingIgnoreCase(Role role, String username);

	Page<User> findByRole(Role role, Pageable pageable);

	Page<User> findByRoleAndUsernameContainingIgnoreCase(Role role, String username, Pageable pageable);
}