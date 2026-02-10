package com.flowerable.spring.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "order_number_sequence",
        uniqueConstraints = @UniqueConstraint(columnNames = "orderDate")
)
@Getter
@NoArgsConstructor
public class OrderNumberSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private int seq;

    public OrderNumberSequence(LocalDate orderDate) {
        this.orderDate = orderDate;
        this.seq = 0;
    }

    public int next() {
        return ++seq;
    }
}
