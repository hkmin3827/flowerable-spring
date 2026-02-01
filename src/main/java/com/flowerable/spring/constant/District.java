package com.flowerable.spring.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum District {

    SEOUL_GANGNAM(Region.SEOUL, "강남구"),
    SEOUL_GANGDONG(Region.SEOUL, "강동구"),
    SEOUL_GANGBUK(Region.SEOUL, "강북구"),
    SEOUL_GANGSEO(Region.SEOUL, "강서구"),
    SEOUL_GWANAK(Region.SEOUL, "관악구"),
    SEOUL_GWANGJIN(Region.SEOUL, "광진구"),
    SEOUL_GURO(Region.SEOUL, "구로구"),
    SEOUL_GEUMCHEON(Region.SEOUL, "금천구"),
    SEOUL_NOWON(Region.SEOUL, "노원구"),
    SEOUL_DOBONG(Region.SEOUL, "도봉구"),
    SEOUL_DONGDAEMUN(Region.SEOUL, "동대문구"),
    SEOUL_DONGJAK(Region.SEOUL, "동작구"),
    SEOUL_MAPO(Region.SEOUL, "마포구"),
    SEOUL_SEODAEMUN(Region.SEOUL, "서대문구"),
    SEOUL_SEOCHO(Region.SEOUL, "서초구"),
    SEOUL_SEONGDONG(Region.SEOUL, "성동구"),
    SEOUL_SEONGBUK(Region.SEOUL, "성북구"),
    SEOUL_SONGPA(Region.SEOUL, "송파구"),
    SEOUL_YANGCHEON(Region.SEOUL, "양천구"),
    SEOUL_YEONGDEUNGPO(Region.SEOUL, "영등포구"),
    SEOUL_YONGSAN(Region.SEOUL, "용산구"),
    SEOUL_EUNPYEONG(Region.SEOUL, "은평구"),
    SEOUL_JONGNO(Region.SEOUL, "종로구"),
    SEOUL_JUNG(Region.SEOUL, "중구"),
    SEOUL_JUNGNANG(Region.SEOUL, "중랑구"),

    GYEONGGI_SUWON(Region.GYEONGGI, "수원시"),
    GYEONGGI_SEONGNAM(Region.GYEONGGI, "성남시"),
    GYEONGGI_GOYANG(Region.GYEONGGI, "고양시"),
    GYEONGGI_YONGIN(Region.GYEONGGI, "용인시"),
    GYEONGGI_BUCHEON(Region.GYEONGGI, "부천시"),
    GYEONGGI_ANSAN(Region.GYEONGGI, "안산시"),
    GYEONGGI_ANYANG(Region.GYEONGGI, "안양시"),
    GYEONGGI_NAMYANGJU(Region.GYEONGGI, "남양주시"),
    GYEONGGI_HWASEONG(Region.GYEONGGI, "화성시"),
    GYEONGGI_PYEONGTAEK(Region.GYEONGGI, "평택시"),

    BUSAN_JUNG(Region.BUSAN, "중구"),
    BUSAN_HAEUNDAE(Region.BUSAN, "해운대구"),
    BUSAN_SUYEONG(Region.BUSAN, "수영구"),
    BUSAN_DONGNAE(Region.BUSAN, "동래구"),
    BUSAN_SASANG(Region.BUSAN, "사상구"),
    BUSAN_GIJANG(Region.BUSAN, "기장군"),

    JEJU_JEJU_SI(Region.JEJU, "제주시"),
    JEJU_SEOGWIPO(Region.JEJU, "서귀포시");

    private final Region region;
    private final String description;

    /** 특정 Region에 속한 District 목록 조회 */
    public static List<District> findByRegion(Region region) {
        return Arrays.stream(values())
                .filter(d -> d.region == region)
                .collect(Collectors.toList());
    }
    public static District fromDescription(String description) {
        return Arrays.stream(values())
                .filter(d -> d.description.equals(description))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid district description: " + description)
                );
    }
    public static District fromSearchParams(String desc){
        if (desc == null) {
            return null;
        }

        return Arrays.stream(values())
                .filter(d -> d.description.equals(desc))
                .findFirst()
                .orElse(null);
    }
}