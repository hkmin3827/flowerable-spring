package com.flowerable.spring.application.order;

import com.flowerable.spring.domain.order.entity.OrderNumberSequence;
import com.flowerable.spring.domain.order.repository.OrderNumberSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderNumberGenerator {
    private final OrderNumberSequenceRepository repository;

    @Transactional
    public String generate() {
        LocalDate today = LocalDate.now();

        OrderNumberSequence seq = repository
                .findByDateForUpdate(today)
                .orElseGet(() -> repository.save(new OrderNumberSequence(today)));

        int nextSeq = seq.next();

        return format(today, nextSeq);
    }

    private String format(LocalDate date, int seq) {
        return String.format(
                "FLW-%s-%06d",
                date.format(DateTimeFormatter.BASIC_ISO_DATE),
                seq
        );
    }
}
