package com.flowerable.spring.controller.flower;


import com.flowerable.spring.constant.flower.Season;
import com.flowerable.spring.dto.common.PageResponse;
import com.flowerable.spring.dto.flower.FlowerRes;
import com.flowerable.spring.service.flower.FlowerService;
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
