package com.flowerable.spring.constant.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("서울특별시"),
    GYEONGGI("경기도"),
    GANGWON("강원특별자치도"),
    GWANGJU("광주광역시"),
    INCHEON("인천광역시"),
    DAEGU("대구광역시"),
    BUSAN("부산광역시"),
    DAEJEON("대전광역시"),
    ULSAN("울산광역시"),
    SEJONG("세종특별자치시"),
    CHUNGBUK("충청북도"),
    CHUNGNAM("충청남도"),
    JEONBUK("전라북도"),
    JEONNAM("전라남도"),
    GYEONGBUK("경상북도"),
    GYEONGNAM("경상남도"),
    JEJU("제주특별자치도");

    private final String description;

    public static Region fromDescription(String description) {
        return Arrays.stream(values())
                .filter(r -> r.description.equals(description))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid region description: " + description)
                );
    }

    public static Region fromSearchParams(String desc){
        if (desc == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(r -> r.description.equals(desc))
                .findFirst()
                .orElse(null);
    }
}
