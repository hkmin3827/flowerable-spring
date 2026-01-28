package com.flowerable.spring.entity.account;

import com.flowerable.spring.constant.AccountStatus;
import com.flowerable.spring.constant.Provider;
import com.flowerable.spring.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "provider_id"})
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider = Provider.LOCAL;

    @Column(name = "provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;   // 로그인 가능 여부 Status

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private Account(String email, String password, Provider provider, String providerId, Role role) {
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.status = AccountStatus.ACTIVE;
    }

    public static Account createLocal(String email, String password, Role role) {
        return new Account(email, password, Provider.LOCAL, null, role);
    }

    public static Account createOAuth(Provider provider, String providerId, Role role) {
        return new Account(null, null, provider, providerId, role);
    }

    public void setEmailIfPresent(String email) {
        if (email != null) this.email = email;
    }

    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }

    public void activate() {
        this.status = AccountStatus.ACTIVE;
    }

    public void softDelete() {
        this.status = AccountStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
