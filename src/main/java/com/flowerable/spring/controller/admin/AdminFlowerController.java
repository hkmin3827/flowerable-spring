package com.flowerable.spring.controller.admin;

import com.flowerable.spring.dto.admin.AdminFlowerListRes;
import com.flowerable.spring.dto.common.PageResponse;
import com.flowerable.spring.dto.flower.FlowerCreateReq;
import com.flowerable.spring.service.admin.AdminFlowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/flowers")
@RequiredArgsConstructor
public class AdminFlowerController {
    private final AdminFlowerService adminFlowerService;

    @PostMapping("/register")
    public Long registerFlower(
            @RequestBody FlowerCreateReq dto
    ){
        return adminFlowerService.registerFlower(dto);
    }

    @PatchMapping("/deactivate/{flowerId}")
    public ResponseEntity<Void> deactiveFlower(
            @PathVariable Long flowerId
    ){
        adminFlowerService.deactivateFlower(flowerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public PageResponse<AdminFlowerListRes> getAllFlowers(
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ){
        return PageResponse.from(adminFlowerService.getAllFlowers(active, pageable));
    }

}
