package com.flowerable.spring.domain.order;

import com.flowerable.spring.domain.auth.Account;
import com.flowerable.spring.domain.auth.AccountRepository;
import com.flowerable.spring.domain.flower.Season;
import com.flowerable.spring.application.flower.dto.FlowerCreateReq;
import com.flowerable.spring.domain.flower.Flower;
import com.flowerable.spring.domain.flower.FlowerRepository;
import com.flowerable.spring.domain.order.constant.OrderStatus;
import com.flowerable.spring.application.order.dto.OrderListRes;
import com.flowerable.spring.domain.order.entity.OrderItem;
import com.flowerable.spring.domain.order.entity.OrderRequest;
import com.flowerable.spring.domain.order.repository.OrderRequestRepository;
import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.domain.shop.Shop;
import com.flowerable.spring.domain.shop.ShopRepository;
import com.flowerable.spring.domain.shopflower.Color;
import com.flowerable.spring.domain.shopflower.ShopFlower;
import com.flowerable.spring.domain.shopflower.ShopFlowerRepository;
import com.flowerable.spring.domain.user.User;
import com.flowerable.spring.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@TestPropertySource(properties ={
        "spring.cloud.aws.parameterstore.enabled=false",
        "spring.config.import="
})
@ActiveProfiles("test")
@Transactional
class OrderRequestRepositoryTest {

    @Autowired
    private OrderRequestRepository orderRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private FlowerRepository flowerRepository;

    @Autowired
    private ShopFlowerRepository shopFlowerRepository;

    @Autowired
    private EntityManager em;

    private User user;
    private Shop shop;
    private ShopFlower shopFlower;
    private OrderRequest savedOrder,savedOrder2;

    @BeforeEach
    void setUp() {
        orderRequestRepository.deleteAll();
        shopFlowerRepository.deleteAll();
        flowerRepository.deleteAll();
        shopRepository.deleteAll();
        userRepository.deleteAll();
        accountRepository.deleteAll();

        Account userAccount = accountRepository.save(
                Account.createUserAccount("user@test.com", "pw", "01011112222")
        );
        user = userRepository.save(User.create(userAccount, "테스트유저"));

        Account shopAccount = accountRepository.save(
                Account.createShopAccount("shop@test.com", "pw", "01033334444")
        );
        shop = createAndSaveShop(shopAccount);

        Flower flower = new Flower(
                FlowerCreateReq.builder()
                        .name("장미").floralLang("사랑").imageUrl("url").category(Season.SPRING)
                        .build()
        );

        ReflectionTestUtils.setField(flower, "createdAt", LocalDateTime.now());
        flowerRepository.save(flower);

        shopFlower = shopFlowerRepository.save(
                new ShopFlower(null, shop, flower, 5000, true, new java.util.HashSet<>())
        );

        OrderItem orderItem = OrderItem.create(shopFlower, 2, Color.RED);
        OrderRequest order = OrderRequest.create(
                "ORD-TEST-001", user, shop, "WHITE", 1000,
                List.of(orderItem), "테스트 주문"
        );

        OrderItem orderItem2 = OrderItem.create(shopFlower, 4, Color.WHITE);
        OrderRequest order2 = OrderRequest.create(
                "ORD-TEST-002", user, shop, "PINK", 1000,
                List.of(orderItem2), "테스트 주문2"
        );

        order.markRequested();

        order2.markRequested();
        order2.changeStatus(OrderStatus.ACCEPTED);

        savedOrder = orderRequestRepository.save(order);
        savedOrder2 = orderRequestRepository.save(order2);


        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("주문번호로 주문 조회 - 성공")
    void findByOrderNumber_existingOrderNumber_returnsOrder() {
        Optional<OrderRequest> result = orderRequestRepository.findByOrderNumber("ORD-TEST-001");

        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo("ORD-TEST-001");
        assertThat(result.get().getShop()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 주문번호 조회 - 빈 결과 반환")
    void findByOrderNumber_nonExistingOrderNumber_returnsEmpty() {
        Optional<OrderRequest> result = orderRequestRepository.findByOrderNumber("INVALID-ORDER");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("userId와 orderId로 주문 조회 - 성공")
    void findByIdAndUserId_validIds_returnsOrder() {
        Optional<OrderRequest> result = orderRequestRepository.findByIdAndUserId(
                savedOrder.getId(), user.getId()
        );

        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo("ORD-TEST-001");
    }

    @Test
    @DisplayName("다른 유저 ID로 주문 조회 - 빈 결과")
    void findByIdAndUserId_wrongUserId_returnsEmpty() {
        Optional<OrderRequest> result = orderRequestRepository.findByIdAndUserId(
                savedOrder.getId(), 9999L
        );

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자 주문 목록 조회 - JPQL 쿼리 확인")
    void findUserOrders_validUserId_returnsOrderList() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderListRes> result = orderRequestRepository.findUserOrders(user.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);

        OrderListRes orderListRes = result.getContent().get(1);
        assertThat(orderListRes.getOrderNumber()).isEqualTo("ORD-TEST-001");
        assertThat(orderListRes.getStatus()).isEqualTo(OrderStatus.REQUESTED);
        assertThat(orderListRes.getTotalPrice()).isEqualTo(11000);
        assertThat(orderListRes.getShopName()).isEqualTo("테스트꽃집");
        assertThat(orderListRes.getUserName()).isEqualTo("테스트유저");

        OrderListRes orderListRes2 = result.getContent().get(0);
        assertThat(orderListRes2.getOrderNumber()).isEqualTo("ORD-TEST-002");
        assertThat(orderListRes2.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(orderListRes2.getTotalPrice()).isEqualTo(21000);
        assertThat(orderListRes2.getShopName()).isEqualTo("테스트꽃집");
        assertThat(orderListRes2.getUserName()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("사용자 주문 목록 - 다른 userId로 조회 시 빈 결과 반환")
    void findUserOrders_wrongUserId_returnsEmpty() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderListRes> result = orderRequestRepository.findUserOrders(9999L, pageable);

        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("샵 주문 목록 조회 - status=REQUESTED 필터")
    void findShopOrders_withRequestedStatus_returnsMatchingOrders() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderListRes> result = orderRequestRepository.findShopOrders(
                shop.getId(), OrderStatus.REQUESTED, pageable
        );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(OrderStatus.REQUESTED);
    }

    @Test
    @DisplayName("샵 주문 목록 - status null이면 전체 조회")
    void findShopOrders_withNullStatus_returnsAllShopOrders() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderListRes> result = orderRequestRepository.findShopOrders(
                shop.getId(), null, pageable
        );

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("샵 주문 상세 조회 - 성공")
    void findShopOrderDetails_validShopAndOrderId_returnsOrder() {
        Optional<OrderRequest> result = orderRequestRepository.findShopOrderDetails(
                shop.getId(), savedOrder.getId()
        );

        assertThat(result).isPresent();
        assertThat(result.get().getOrderNumber()).isEqualTo("ORD-TEST-001");
        assertThat(result.get().getOrderItems()).isNotEmpty();
    }

    @Test
    @DisplayName("다른 샵 ID로 주문 상세 조회 - 빈 결과")
    void findShopOrderDetails_wrongShopId_returnsEmpty() {
        Optional<OrderRequest> result = orderRequestRepository.findShopOrderDetails(
                9999L, savedOrder.getId()
        );

        assertThat(result).isEmpty();
    }

    private Shop createAndSaveShop(Account shopAccount) {
        Shop shop = new Shop(shopAccount, "테스트꽃집", "테헤란로1234", Region.SEOUL, District.SEOUL_GANGNAM);
        ReflectionTestUtils.setField(shop, "status", com.flowerable.spring.domain.shop.constant.ShopStatus.ACTIVE);
        ReflectionTestUtils.setField(shop, "registerAt", java.time.LocalDateTime.now());
        return shopRepository.save(shop);
    }
}
