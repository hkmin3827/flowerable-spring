package com.flowerable.spring.entity.user;

import com.flowerable.spring.dto.user.UserUpdateInfoReq;
import com.flowerable.spring.entity.account.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Account와 1:1
     * account_id는 전역 인증 PK를 가리킴
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    private String profileImageUrl;

    @Column(length = 30)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    private User(Account account, String name) {
        this.account = account;
        this.name = name;
    }

    public void update(UserUpdateInfoReq req){
        if(req.getName() != null){
            this.name = req.getName();
        }
        if(req.getTelnum() != null){
            this.account.setTelnum(req.getTelnum());
        }
    }

    public static User create(Account account, String name) {
        return new User(account, name);
    }

    public void deactivate() {
        this.active = false;
    }
    public void activate() {
        this.active = true;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.active = false;
    }
}
