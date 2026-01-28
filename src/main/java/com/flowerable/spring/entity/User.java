package com.flowerable.spring.entity;


import com.flowerable.spring.constant.Provider;
import com.flowerable.spring.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "provider_id"})
        }

)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;

    @Column(nullable = false)
    private Provider provider = Provider.LOCAL;
    private String providerId;

    @Column(nullable = true)
    private String profileImageUrl;   // 보류 (굳이)

    @Column(length = 30)
    private String name;

    @Column(nullable = true)
    private String address;

    // Role : USER, ADMIN
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    @Column(length = 20)
    private String telnum;

    @Column(nullable = false)
    private boolean active = true;   // 연속적인 주문취소 = 관리자가 비활성

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @PrePersist
    public void onCreate() {this.createdAt = LocalDateTime.now();}

    // 회원 자진 탈퇴 (데이터 보존 목적)
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }

    // 관리자 권한
    public void activate() {
        this.active = true;
    }
    public void deactivate() {
        this.active = false;
    }


    private User(
            String email,
            String password,
            Provider provider,
            Role role,
            String name,
            String telnum
    ) {
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.role = role;
        this.name = name;
        this.telnum = telnum;
    }

    public static User createLocalUser(
            String email,
            String encodedPassword,
            String name,
            String telnum
    ) {
        return new User(
                email,
                encodedPassword,
                Provider.LOCAL,
                Role.ROLE_USER,
                name,
                telnum
        );
    }
    private User(
            Provider provider,
            String providerId,
            Role role
    ) {
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }
    public static User createOAuthUser(
            Provider provider,
            String providerId
    ){
        return new User(
                provider,
                providerId,
                Role.ROLE_USER
        );
    }
    public void updateAddress(String address) {
        this.address = address;
    }
    public void initOAuthInfo(String email, String name) {
        if (email != null) {
            this.email = email;
        }
        if (name != null) {
            this.name = name;
        }
    }
}
