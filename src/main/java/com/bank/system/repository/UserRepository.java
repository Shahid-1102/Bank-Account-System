package com.bank.system.repository;

import com.bank.system.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username. Used for login.
     * @param username The username to search for.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email. Used for checking duplicates during registration.
     * @param email The email to search for.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a user exists with the given username. More efficient than findByUsername.
     * @param username The username to check.
     * @return true if a user with this username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     * @param email The email to check.
     * @return true if a user with this email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}