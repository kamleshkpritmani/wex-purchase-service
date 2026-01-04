package com.wex.purchase.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wex.purchase.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}