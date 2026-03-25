package com.flowerable.spring.domain.user.service;

import com.flowerable.spring.domain.auth.constant.AccountStatus;
import com.flowerable.spring.domain.auth.entity.Account;
import com.flowerable.spring.domain.auth.repository.AccountRepository;
import com.flowerable.spring.domain.cart.repository.CartItemDetailRepository;
import com.flowerable.spring.domain.cart.repository.CartItemRepository;
import com.flowerable.spring.domain.chat.repository.ChatRoomRepository;
import com.flowerable.spring.domain.notification.constant.NotificationReceiverType;
import com.flowerable.spring.domain.notification.repository.NotificationRepository;
import com.flowerable.spring.domain.order.repository.OrderCancelLogRepository;
import com.flowerable.spring.domain.order.repository.OrderItemRepository;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.payment.repository.PaymentRepository;
import com.flowerable.spring.domain.user.entity.User;
import com.flowerable.spring.domain.user.repository.UserRepository;
import com.flowerable.spring.global.exception.AccountNotFoundException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestUserCleanupService {

    private static final String TEST_ACCOUNT_EMAIL = "test@flowerable.com";

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final OrderRequestRepository orderRequestRepository;
    private final OrderCancelLogRepository orderCancelLogRepository;
    private final PaymentRepository paymentRepository;
    private final CartItemDetailRepository cartItemDetailRepository;
    private final CartItemRepository cartItemRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final OrderItemRepository orderItemRepository;


    @Transactional
    public void cleanupTestUser() {
        Account account = accountRepository.findByEmail(TEST_ACCOUNT_EMAIL)
                .orElseThrow(AccountNotFoundException::new);

        User user = userRepository.findByAccountId(account.getId())
                .orElseThrow(UserNotFoundException::new);
        if(account.getStatus().equals(AccountStatus.DELETED)) {
            user.rollbackSoftDelete();
            account.rollbackSoftDelete();
        }

        log.info("[TestAccountCleanup] testEmail = {} cleanup 시작", account.getEmail());
        try {
            cleanup(user.getId());
            log.info("[TestAccountCleanup] testEmail = {} cleanup 완료", account.getEmail());
        } catch (Exception e) {
            log.error("[TestAccountCleanup] testEmail={} cleanup 실패: {}", account.getEmail(), e.getMessage(), e);
            throw e;
        }
    }

    public void cleanup(Long userId) {

        notificationRepository.deleteByReceiverTypeAndReceiverId(
                NotificationReceiverType.USER, userId);

        List<Long> orderIds = orderRequestRepository.findOrderIdsByUserId(userId);
        if (!orderIds.isEmpty()) {
            notificationRepository.deleteShopNotificationsByOrderIds(orderIds);
        }

        cartItemDetailRepository.deleteAllByCartUserId(userId);
        cartItemRepository.deleteAllByCartUserId(userId);

        chatRoomRepository.deleteAllByUserId(userId);

        if (!orderIds.isEmpty()) {
            orderCancelLogRepository.deleteAllByOrderRequestIds(orderIds);
            paymentRepository.deleteAllByOrderIds(orderIds);
            orderItemRepository.deleteAllByOrderRequestIdIn(orderIds);
        }

        orderRequestRepository.deleteAllByUserId(userId);
    }
}