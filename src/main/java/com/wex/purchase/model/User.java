package com.wex.purchase.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    private boolean enabled = true;
    
    @Column(name = "account_locked")
    private boolean accountLocked = false;
}