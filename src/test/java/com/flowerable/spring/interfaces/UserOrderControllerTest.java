package com.flowerable.spring.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowerable.spring.domain.auth.constant.Role;
import com.flowerable.spring.application.bouquet.BouquetPreviewService;
import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.application.order.dto.*;
import com.flowerable.spring.application.order.UserOrderService;
import com.flowerable.spring.domain.shopflower.Color;
import com.flowerable.spring.application.wrappingoption.WrappingOptionRes;
import com.flowerable.spring.application.wrappingoption.WrappingOptionService;
import com.flowerable.spring.global.exception.OrderNotFoundException;
import com.flowerable.spring.global.security.CustomUserDetails;
import com.flowerable.spring.interfaces.order.UserOrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserOrderController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                com.flowerable.spring.global.jwt.JwtAuthenticationFilter.class,
                                com.flowerable.spring.global.config.SecurityConfig.class
                        }
                )
        }
)
@Import(com.flowerable.spring.global.config.TestSecurityConfig.class)
@TestPropertySource(properties ={
        "spring.cloud.aws.parameterstore.enabled=false",
        "spring.config.import="
})
class UserOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserOrderService userOrderService;

    @MockitoBean
    private WrappingOptionService wrappingOptionService;

    @MockitoBean
    private BouquetPreviewService bouquetPreviewService;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetails = new CustomUserDetails(1L, Role.ROLE_USER);
    }

    @Test
    @DisplayName("POST /api/orders/users/{shopId} - 주문 생성 성공 (200)")
    void createOrder_validRequest_returns200() throws Exception {
        Long shopId = 1L;
        OrderItemReq itemReq = new OrderItemReq(10L, 2, Color.RED);
        OrderCreateReq createReq = new OrderCreateReq();
        ReflectionTestUtils.setField(createReq, "wrappingColorName", "WHITE");
        ReflectionTestUtils.setField(createReq, "wrappingExtraPrice", 1000);
        ReflectionTestUtils.setField(createReq, "orderItems", List.of(itemReq));
        ReflectionTestUtils.setField(createReq, "message", "빠른 배송 부탁드립니다.");

        OrderCreateRes createRes = new OrderCreateRes(100L, "ORD-TEST-001", 11000);

        given(userOrderService.createOrder(eq(1L), eq(shopId), any(OrderCreateReq.class)))
                .willReturn(createRes);

        mockMvc.perform(post("/api/orders/users/{shopId}", shopId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100))
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST-001"))
                .andExpect(jsonPath("$.totalPrice").value(11000));
    }


    @Test
    @DisplayName("GET /api/orders/users - 내 주문 목록 조회 성공 (200)")
    void getMyOrders_authenticatedUser_returns200() throws Exception {
        OrderListRes orderListRes = new OrderListRes(
                1L, "ORD-TEST-001", OrderStatus.REQUESTED,
                10000, LocalDateTime.now(), "테스트꽃집", "테스트유저", 2L
        );
        Page<OrderListRes> page = new PageImpl<>(
                List.of(orderListRes), PageRequest.of(0, 10), 1
        );

        given(userOrderService.getMyOrders(eq(1L), any(Pageable.class))).willReturn(page);

        mockMvc.perform(get("/api/orders/users")
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD-TEST-001"))
                .andExpect(jsonPath("$.content[0].status").value("REQUESTED"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/orders/users - 주문 없을 때 빈 목록 반환")
    void getMyOrders_noOrders_returnsEmptyContent() throws Exception {
        Page<OrderListRes> emptyPage = new PageImpl<>(
                List.of(), PageRequest.of(0, 10), 0
        );

        given(userOrderService.getMyOrders(any(), any(Pageable.class))).willReturn(emptyPage);

        mockMvc.perform(get("/api/orders/users")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }


    @Test
    @DisplayName("GET /api/orders/users/{orderId} - 주문 상세 조회 성공 (200)")
    void getOrderDetails_existingOrder_returns200() throws Exception {
        Long orderId = 1L;
        OrderDetailRes detailRes = OrderDetailRes.builder()
                .orderId(orderId)
                .orderNumber("ORD-TEST-001")
                .status(OrderStatus.REQUESTED)
                .userId(10L)
                .shopId(20L)
                .shopName("테스트꽃집")
                .userName("테스트유저")
                .totalFlowerPrice(10000)
                .wrappingExtraPrice(1000)
                .totalPrice(11000)
                .wrappingColorName("WHITE")
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();

        given(userOrderService.getOrderDetails(eq(1L), eq(orderId))).willReturn(detailRes);

        mockMvc.perform(get("/api/orders/users/{orderId}", orderId)
                        .with(user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST-001"))
                .andExpect(jsonPath("$.status").value("REQUESTED"))
                .andExpect(jsonPath("$.totalPrice").value(11000))
                .andExpect(jsonPath("$.shopName").value("테스트꽃집"));
    }

    @Test
    @DisplayName("GET /api/orders/users/{orderId} - 존재하지 않는 주문 (404)")
    void getOrderDetails_nonExistingOrder_returns404() throws Exception {
        Long nonExistingOrderId = 9999L;
        given(userOrderService.getOrderDetails(any(), eq(nonExistingOrderId)))
                .willThrow(new OrderNotFoundException());

        mockMvc.perform(get("/api/orders/users/{orderId}", nonExistingOrderId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/orders/users/{orderId}/cancel - 주문 취소 성공 (204)")
    void cancelOrder_validRequest_returns204() throws Exception {
        Long orderId = 1L;
        doNothing().when(userOrderService).cancelOrder(1L, orderId);

        mockMvc.perform(patch("/api/orders/users/{orderId}/cancel", orderId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH /api/orders/users/{orderId}/cancel - 이미 수락된 주문 취소 (400)")
    void cancelOrder_alreadyAccepted_returns400() throws Exception {
        Long orderId = 1L;
        doThrow(new com.flowerable.spring.global.exception.CustomException(
                com.flowerable.spring.global.constant.ErrorCode.ORDER_ALREADY_ACCEPTED))
                .when(userOrderService).cancelOrder(any(), eq(orderId));

        mockMvc.perform(patch("/api/orders/users/{orderId}/cancel", orderId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/orders/users/{shopId}/wrapping-options - 포장 옵션 조회 성공 (200)")
    void getShopWrappingOptions_validShopId_returns200() throws Exception {
        Long shopId = 1L;
        WrappingOptionRes wrappingOptionRes = WrappingOptionRes.builder()
                .shopId(shopId)
                .colorNames(List.of("WHITE", "PINK"))
                .price(1000)
                .build();

        given(wrappingOptionService.getShopWrappingOption(shopId)).willReturn(wrappingOptionRes);

        mockMvc.perform(get("/api/orders/users/{shopId}/wrapping-options", shopId)
                        .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shopId").value(shopId));
    }

}
