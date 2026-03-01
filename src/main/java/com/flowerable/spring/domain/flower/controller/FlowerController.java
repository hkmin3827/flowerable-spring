package com.flowerable.spring.domain.flower.controller;


import com.flowerable.spring.domain.flower.constant.Season;
import com.flowerable.spring.global.dto.PageResponse;
import com.flowerable.spring.domain.flower.dto.FlowerRes;
import com.flowerable.spring.domain.flower.service.FlowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flowers")
@RequiredArgsConstructor
public class FlowerController {
    private final FlowerService flowerService;

    @GetMapping
    public PageResponse<FlowerRes> getActiveFlowers(
            @RequestParam(required = false) Season category,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        return PageResponse.from(flowerService.getActiveFlowers(category, pageable));
    }

}
