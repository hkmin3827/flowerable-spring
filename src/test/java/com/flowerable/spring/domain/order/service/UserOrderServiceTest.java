package com.flowerable.spring.domain.order.service;

import com.flowerable.spring.domain.auth.entity.Account;
import com.flowerable.spring.domain.flower.constant.Season;
import com.flowerable.spring.domain.flower.dto.FlowerCreateReq;
import com.flowerable.spring.domain.flower.entity.Flower;
import com.flowerable.spring.domain.notification.service.NotificationService;
import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.domain.order.dto.*;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.order.repository.OrderCancelLogRepository;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.payment.service.PaymentService;
import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.domain.shop.entity.Shop;
import com.flowerable.spring.domain.shop.repository.ShopRepository;
import com.flowerable.spring.domain.shopflower.constant.Color;
import com.flowerable.spring.domain.shopflower.entity.ShopFlower;
import com.flowerable.spring.domain.shopflower.repository.ShopFlowerRepository;
import com.flowerable.spring.domain.user.entity.User;
import com.flowerable.spring.domain.user.repository.UserRepository;
import com.flowerable.spring.global.constant.ErrorCode;
import com.flowerable.spring.global.exception.CustomException;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import com.flowerable.spring.global.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class UserOrderServiceTest {

    @InjectMocks
    private UserOrderService userOrderService;

    @Mock
    private OrderRequestRepository orderRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ShopFlowerRepository shopFlowerRepository;

    @Mock
    private OrderCancelLogService orderCancelLogService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private OrderNumberGenerator orderNumberGenerator;

    @Mock
    private OrderCancelLogRepository orderCancelLogRepository;

    @Mock
    private PaymentService paymentService;

    private Account userAccount;
    private User user;
    private Account shopAccount;
    private Shop shop;
    private ShopFlower shopFlower;
    private Flower flower;

    @BeforeEach
    void setUp() {
        userAccount = Account.createUserAccount("user@example.com", "pw", "01011112222");
        ReflectionTestUtils.setField(userAccount, "id", 1L);
        user = User.create(userAccount, "테스트유저");
        ReflectionTestUtils.setField(user, "id", 10L);

        shopAccount = Account.createShopAccount("shop@example.com", "pw", "01033334444");
        ReflectionTestUtils.setField(shopAccount, "id", 2L);
        shop = Shop.create(shopAccount, "테스트꽃집", "테헤란로 123", Region.SEOUL, District.SEOUL_GANGNAM);
        ReflectionTestUtils.setField(shop, "id", 20L);

        flower = new Flower(FlowerCreateReq.builder()
                .name("장미").floralLang("사랑").imageUrl("url").category(Season.SPRING).build());
        ReflectionTestUtils.setField(flower, "id", 100L);


        shopFlower = new ShopFlower(200L, shop, flower, 5000, true, new HashSet<>());
    }


    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_validRequest_returnsOrderCreateRes() {
        Long accountId = 1L;
        Long shopId = 20L;

        OrderItemReq itemReq = new OrderItemReq(200L, 2, Color.RED);
        OrderCreateReq req = new OrderCreateReq();
        ReflectionTestUtils.setField(req, "wrappingColorName", "WHITE");
        ReflectionTestUtils.setField(req, "wrappingExtraPrice", 1000);
        ReflectionTestUtils.setField(req, "orderItems", List.of(itemReq));
        ReflectionTestUtils.setField(req, "message", "빠른 배송 부탁드립니다.");

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId)).willReturn(Optional.of(user));
        given(shopRepository.findByIdAndDeletedAtIsNullAndIsActive(shopId)).willReturn(Optional.of(shop));
        given(shopFlowerRepository.findByIdAndShopIdAndOnSaleTrue(200L, shopId)).willReturn(Optional.of(shopFlower));
        given(orderNumberGenerator.generate()).willReturn("ORD-20240101-001");
        given(orderRequestRepository.save(any(OrderRequest.class))).willAnswer(invocation -> {
            OrderRequest order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 999L);
            return order;
        });

        OrderCreateRes result = userOrderService.createOrder(accountId, shopId, req);

        assertThat(result).isNotNull();
        assertThat(result.getOrderNumber()).isEqualTo("ORD-20240101-001");
        assertThat(result.getTotalPrice()).isEqualTo(11000); // 5000 * 2 + 1000

        verify(orderRequestRepository).save(any(OrderRequest.class));
    }

    @Test
    @DisplayName("주문 생성 - 유저 없으면 UserNotFoundException 발생")
    void createOrder_userNotFound_throwsException() {
        given(userRepository.findByAccountIdAndDeletedAtIsNull(any())).willReturn(Optional.empty());

        OrderCreateReq req = new OrderCreateReq();
        ReflectionTestUtils.setField(req, "orderItems", List.of());

        assertThatThrownBy(() -> userOrderService.createOrder(1L, 20L, req))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("주문 생성 - 샵 없으면 CustomException(SHOP_ORDER_CREATE_FAIL) 발생")
    void createOrder_shopNotFound_throwsCustomException() {
        given(userRepository.findByAccountIdAndDeletedAtIsNull(any())).willReturn(Optional.of(user));
        given(shopRepository.findByIdAndDeletedAtIsNullAndIsActive(any())).willReturn(Optional.empty());

        OrderCreateReq req = new OrderCreateReq();
        ReflectionTestUtils.setField(req, "orderItems", List.of());

        assertThatThrownBy(() -> userOrderService.createOrder(1L, 20L, req))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.SHOP_ORDER_CREATE_FAIL.getMessage());
    }


    @Test
    @DisplayName("내 주문 목록 조회 성공")
    void getMyOrders_validAccountId_returnsOrderListPage() {
        Long accountId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        OrderListRes orderListRes = new OrderListRes(
                1L, "ORD-001", OrderStatus.REQUESTED,
                10000, LocalDateTime.now(), "테스트꽃집", "테스트유저", 2L
        );
        Page<OrderListRes> page = new PageImpl<>(List.of(orderListRes), pageable, 1);

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId)).willReturn(Optional.of(user));
        given(orderRequestRepository.findUserOrders(10L, pageable)).willReturn(page);

        Page<OrderListRes> result = userOrderService.getMyOrders(accountId, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getOrderNumber()).isEqualTo("ORD-001");
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(OrderStatus.REQUESTED);
    }

    @Test
    @DisplayName("내 주문 목록 - 유저 없으면 UserNotFoundException 발생")
    void getMyOrders_userNotFound_throwsException() {
        given(userRepository.findByAccountIdAndDeletedAtIsNull(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> userOrderService.getMyOrders(999L, PageRequest.of(0, 10)))
                .isInstanceOf(UserNotFoundException.class);
    }


    @Test
    @DisplayName("주문 취소 - 이미 수락된 주문 취소 시도 - 예외 발생")
    void cancelOrder_alreadyAccepted_throwsCustomException() {
        Long accountId = 1L;
        Long orderId = 1L;

        OrderRequest order = mock(OrderRequest.class);
        given(order.getStatus()).willReturn(OrderStatus.ACCEPTED);

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId)).willReturn(Optional.of(user));
        given(orderRequestRepository.findByIdAndUserId(orderId, 10L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> userOrderService.cancelOrder(accountId, orderId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ORDER_ALREADY_ACCEPTED.getMessage());
    }

    @Test
    @DisplayName("주문 취소 - 이미 취소된 주문 다시 취소 시도 - 예외 발생")
    void cancelOrder_alreadyCanceled_throwsCustomException() {
        Long accountId = 1L;
        Long orderId = 1L;

        OrderRequest order = mock(OrderRequest.class);
        given(order.getStatus()).willReturn(OrderStatus.CANCELED);

        given(userRepository.findByAccountIdAndDeletedAtIsNull(accountId)).willReturn(Optional.of(user));
        given(orderRequestRepository.findByIdAndUserId(orderId, 10L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> userOrderService.cancelOrder(accountId, orderId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ORDER_ALREADY_CANCELED.getMessage());
    }

    @Test
    @DisplayName("주문 취소 - 주문 없으면 OrderNotFoundException 발생")
    void cancelOrder_orderNotFound_throwsOrderNotFoundException() {
        given(userRepository.findByAccountIdAndDeletedAtIsNull(any())).willReturn(Optional.of(user));
        given(orderRequestRepository.findByIdAndUserId(any(), any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> userOrderService.cancelOrder(1L, 999L))
                .isInstanceOf(OrderNotFoundException.class);
    }

}
