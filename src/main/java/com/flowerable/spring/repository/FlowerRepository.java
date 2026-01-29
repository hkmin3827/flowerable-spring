package com.flowerable.spring.repository;

import com.flowerable.spring.entity.flower.Flower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlowerRepository extends JpaRepository<Flower, Long> {
    Optional<Flower> findByName(String name);

    boolean existsByName(String name);

    List<Flower> findAllByActiveTrue();
}
