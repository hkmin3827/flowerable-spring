package com.flowerable.spring.domain.admin.controller;

import com.flowerable.spring.domain.admin.dto.AdminFlowerListRes;
import com.flowerable.spring.global.dto.PageResponse;
import com.flowerable.spring.domain.flower.dto.FlowerCreateReq;
import com.flowerable.spring.domain.flower.dto.FlowerUpdateInfoReq;
import com.flowerable.spring.domain.admin.service.AdminFlowerService;
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

    @GetMapping("/list")
    public PageResponse<AdminFlowerListRes> getAllFlowers(
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ){
        return PageResponse.from(adminFlowerService.getAllFlowers(active, pageable));
    }

    @PatchMapping("/{flowerId}/activate")
    public ResponseEntity<Void> activeFlower(
            @PathVariable Long flowerId
    ){
        adminFlowerService.activateFlower(flowerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{flowerId}/deactivate")
    public ResponseEntity<Void> deactiveFlower(
            @PathVariable Long flowerId
    ){
        adminFlowerService.deactivateFlower(flowerId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{flowerId}/update")
    public ResponseEntity<Void> updateFlowerInfo(
            @PathVariable Long flowerId,
            @RequestBody FlowerUpdateInfoReq req
            ){
        adminFlowerService.updateFlowerInfo(flowerId, req);

        return ResponseEntity.noContent().build();
    }
}
