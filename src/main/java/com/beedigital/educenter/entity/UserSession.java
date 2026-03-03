package com.beedigital.educenter.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    private String ipAddress;
    private String userAgent;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime loginTime = LocalDateTime.now();

    private LocalDateTime logoutTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

