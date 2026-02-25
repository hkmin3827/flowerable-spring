package com.flowerable.spring.service.auth.password;

import com.flowerable.spring.constant.auth.TokenType;
import com.flowerable.spring.constant.common.ErrorCode;
import com.flowerable.spring.entity.account.Account;
import com.flowerable.spring.exception.CustomException;
import com.flowerable.spring.exception.UserNotFoundException;
import com.flowerable.spring.jwt.JwtProvider;
import com.flowerable.spring.jwt.RefreshTokenService;
import com.flowerable.spring.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;


@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;


    public void sendResetLink(String email) {

        Account account = accountRepository
                .findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(UserNotFoundException::new);

        String resetToken = jwtProvider.createPasswordResetToken(
                account.getId(),
                account.getRole(),
                Duration.ofMinutes(10)
        );

        String resetUrl =
                "http://localhost:3000/reset-password?token=" + resetToken;

        mailService.sendPasswordResetMail(email, resetUrl);
    }

    public void resetPassword(String token, String newPassword) {

        if (jwtProvider.getTokenType(token) != TokenType.PASSWORD_RESET) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Long accountId = jwtProvider.getId(token);


        Account account = accountRepository.findById(accountId)
                .orElseThrow(UserNotFoundException::new);

        account.updatePassword(passwordEncoder.encode(newPassword));

        refreshTokenService.deleteRefreshToken(accountId);
    }
}
