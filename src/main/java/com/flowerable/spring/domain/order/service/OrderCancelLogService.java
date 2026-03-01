package com.flowerable.spring.domain.order.service;

import com.flowerable.spring.domain.order.constant.OrderCancelBy;
import com.flowerable.spring.domain.order.constant.OrderCancelReason;
import com.flowerable.spring.domain.order.entity.OrderCancelLog;
import com.flowerable.spring.domain.order.repository.OrderCancelLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCancelLogService {

    private final OrderCancelLogRepository orderCancelLogRepository;

    @Transactional
    public void recordCancel(Long orderRequestId, OrderCancelBy canceledBy, OrderCancelReason cancelReason) {
        if (orderCancelLogRepository.findByOrderRequestId(orderRequestId).isPresent()) {
            return;
        }
        orderCancelLogRepository.save(OrderCancelLog.create(orderRequestId, canceledBy, cancelReason));
    }
}