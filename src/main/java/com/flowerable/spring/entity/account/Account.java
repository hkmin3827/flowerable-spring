package com.flowerable.spring.entity.account;

import com.flowerable.spring.constant.auth.AccountStatus;
import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.constant.auth.Provider;
import com.flowerable.spring.constant.auth.Role;
import com.flowerable.spring.exception.CustomException;
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

    public static Account createUserAccount(String email, String password) {
        return new Account(email, password, Provider.LOCAL, null,  Role.ROLE_USER);
    }

    public static Account createShopAccount(String email, String password) {
        return new Account(email, password, Provider.LOCAL, null, Role.ROLE_SHOP);
    }

    public static Account createOAuth(Provider provider, String providerId, Role role) {
        return new Account(null, null, provider, providerId, role);
    }

    public void setEmailIfPresent(String email) {
        if (email != null) this.email = email;
    }

    public void suspend() {
        if(this.status == AccountStatus.SUSPENDED) {
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_INACTIVE);
        }
        this.status = AccountStatus.SUSPENDED;
    }

    public void activate() {
        if(this.status == AccountStatus.ACTIVE) {
            throw new CustomException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
        }
        this.status = AccountStatus.ACTIVE;
    }

    public void softDelete() {
        this.status = AccountStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
