package com.flowerable.spring.domain.admin.dto;

import com.flowerable.spring.domain.order.constant.OrderStatus;
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
}
