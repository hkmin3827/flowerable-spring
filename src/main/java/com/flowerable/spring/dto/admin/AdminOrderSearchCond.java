package com.flowerable.spring.dto.admin;

import com.flowerable.spring.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminOrderSearchCond {
    private OrderStatus status;
    private Long userId;
    private Long shopId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime from;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime to;

//    from  → 이 시간 이후(>=)에 생성된 주문만 조회
//    to    → 이 시간 이전(<=)에 생성된 주문만 조회
}
