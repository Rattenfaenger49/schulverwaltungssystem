package com.school_system.entity.security;

import com.school_system.enums.authentication.TokenType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String token;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private TokenType tokenType;

    private boolean revoked;

    @ManyToOne
    private User user;
}
