package com.flowerable.spring.interfaces.shop;

import com.flowerable.spring.domain.shop.constant.District;
import com.flowerable.spring.domain.shop.constant.Region;
import com.flowerable.spring.application.shop.dto.DistrictRes;
import com.flowerable.spring.application.shop.dto.RegionRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {
    @GetMapping
    public ResponseEntity<List<RegionRes>> getRegions() {
        List<RegionRes> responses = Arrays.stream(Region.values())
                .map(r -> new RegionRes(r.name(), r.getDescription()))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/districts")
    public ResponseEntity<List<DistrictRes>> getDistricts(@RequestParam Region region) {
        List<DistrictRes> responses = District.findByRegion(region).stream()
                .sorted(Comparator.comparing(District::getDescription))
                .map(d -> new DistrictRes(d.name(), d.getDescription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
