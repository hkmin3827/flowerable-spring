package com.flowerable.spring.interfaces;

import com.flowerable.spring.application.payment.dto.PaymentConfirmReq;
import com.flowerable.spring.application.payment.PaymentConfirmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentConfirmService paymentConfirmService;

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Long>> confirm(@RequestBody PaymentConfirmReq req) {
        Long orderId = paymentConfirmService.processPay(req);
        return ResponseEntity.ok(Map.of("orderId", orderId));
    }
}