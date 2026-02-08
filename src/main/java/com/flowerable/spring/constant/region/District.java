package com.flowerable.spring.constant.region;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum District {

    SEOUL_JONGNO(Region.SEOUL, "종로구"), SEOUL_JUNGGU(Region.SEOUL, "중구"), SEOUL_YONGSAN(Region.SEOUL, "용산구"),
    SEOUL_SEONGDONG(Region.SEOUL, "성동구"), SEOUL_GWANGJIN(Region.SEOUL, "광진구"), SEOUL_DONGDAEMUN(Region.SEOUL, "동대문구"),
    SEOUL_JUNGNANG(Region.SEOUL, "중랑구"), SEOUL_SEONGBUK(Region.SEOUL, "성북구"), SEOUL_GANGBUK(Region.SEOUL, "강북구"),
    SEOUL_DOBONG(Region.SEOUL, "도봉구"), SEOUL_NOWON(Region.SEOUL, "노원구"), SEOUL_EUNPYEONG(Region.SEOUL, "은평구"),
    SEOUL_SEODAEMUN(Region.SEOUL, "서대문구"), SEOUL_MAPO(Region.SEOUL, "마포구"), SEOUL_YANGCHEON(Region.SEOUL, "양천구"),
    SEOUL_GANGSEO(Region.SEOUL, "강서구"), SEOUL_GURO(Region.SEOUL, "구로구"), SEOUL_GEUMCHEON(Region.SEOUL, "금천구"),
    SEOUL_YEONGDEUNGPO(Region.SEOUL, "영등포구"), SEOUL_DONGJAK(Region.SEOUL, "동작구"), SEOUL_GWANAK(Region.SEOUL, "관악구"),
    SEOUL_SEOCHO(Region.SEOUL, "서초구"), SEOUL_GANGNAM(Region.SEOUL, "강남구"), SEOUL_SONGPA(Region.SEOUL, "송파구"),
    SEOUL_GANGDONG(Region.SEOUL, "강동구"),

    GYEONGGI_SUWON(Region.GYEONGGI, "수원시"), GYEONGGI_SEONGNAM(Region.GYEONGGI, "성남시"), GYEONGGI_UIJEONGBU(Region.GYEONGGI, "의정부시"),
    GYEONGGI_ANYANG(Region.GYEONGGI, "안양시"), GYEONGGI_BUCHEON(Region.GYEONGGI, "부천시"), GYEONGGI_GWANGMYEONG(Region.GYEONGGI, "광명시"),
    GYEONGGI_PYEONGTAEK(Region.GYEONGGI, "평택시"), GYEONGGI_DONGDUCHEON(Region.GYEONGGI, "동두천시"), GYEONGGI_ANSAN(Region.GYEONGGI, "안산시"),
    GYEONGGI_GOYANG(Region.GYEONGGI, "고양시"), GYEONGGI_GWACHEON(Region.GYEONGGI, "과천시"), GYEONGGI_GURI(Region.GYEONGGI, "구리시"),
    GYEONGGI_NAMYANGJU(Region.GYEONGGI, "남양주시"), GYEONGGI_OSAN(Region.GYEONGGI, "오산시"), GYEONGGI_SIHEUNG(Region.GYEONGGI, "시흥시"),
    GYEONGGI_GUNPO(Region.GYEONGGI, "군포시"), GYEONGGI_UIWANG(Region.GYEONGGI, "의왕시"), GYEONGGI_HANAM(Region.GYEONGGI, "하남시"),
    GYEONGGI_YONGIN(Region.GYEONGGI, "용인시"), GYEONGGI_PAJU(Region.GYEONGGI, "파주시"), GYEONGGI_ICHON(Region.GYEONGGI, "이천시"),
    GYEONGGI_ANSEONG(Region.GYEONGGI, "안성시"), GYEONGGI_GIMPO(Region.GYEONGGI, "김포시"), GYEONGGI_HWASEONG(Region.GYEONGGI, "화성시"),
    GYEONGGI_GWANGJU(Region.GYEONGGI, "광주시"), GYEONGGI_YANGJU(Region.GYEONGGI, "양주시"), GYEONGGI_POCHEON(Region.GYEONGGI, "포천시"),
    GYEONGGI_YEOJU(Region.GYEONGGI, "여주시"), GYEONGGI_YEONCHEON(Region.GYEONGGI, "연천군"), GYEONGGI_GAPYEONG(Region.GYEONGGI, "가평군"),
    GYEONGGI_YANGPYEONG(Region.GYEONGGI, "양평군"),

    INCHEON_JUNGGU(Region.INCHEON, "중구"), INCHEON_DONGGU(Region.INCHEON, "동구"), INCHEON_MICHUHOL(Region.INCHEON, "미추홀구"),
    INCHEON_YEONSUGU(Region.INCHEON, "연수구"), INCHEON_NAMDONGGU(Region.INCHEON, "남동구"), INCHEON_BUPYEONGGU(Region.INCHEON, "부평구"),
    INCHEON_GYEYANGGU(Region.INCHEON, "계양구"), INCHEON_SEOGU(Region.INCHEON, "서구"), INCHEON_GANGHWA(Region.INCHEON, "강화군"),
    INCHEON_ONGJIN(Region.INCHEON, "옹진군"),

    DAEGU_JUNGGU(Region.DAEGU, "중구"), DAEGU_DONGGU(Region.DAEGU, "동구"), DAEGU_SEOGU(Region.DAEGU, "서구"),
    DAEGU_NAMGU(Region.DAEGU, "남구"), DAEGU_BUKGU(Region.DAEGU, "북구"), DAEGU_SUSEONGGU(Region.DAEGU, "수성구"),
    DAEGU_DALSEOGU(Region.DAEGU, "달서구"), DAEGU_DALSEONG(Region.DAEGU, "달성군"), DAEGU_GUNWI(Region.DAEGU, "군위군"),

    BUSAN_JUNGGU(Region.BUSAN, "중구"), BUSAN_SEOGU(Region.BUSAN, "서구"), BUSAN_DONGGU(Region.BUSAN, "동구"),
    BUSAN_YEONGDOGU(Region.BUSAN, "영도구"), BUSAN_BUSANJINGU(Region.BUSAN, "부산진구"), BUSAN_DONGNAEGU(Region.BUSAN, "동래구"),
    BUSAN_NAMGU(Region.BUSAN, "남구"), BUSAN_BUKGU(Region.BUSAN, "북구"), BUSAN_HAEUNDAEGU(Region.BUSAN, "해운대구"),
    BUSAN_SAHAGU(Region.BUSAN, "사하구"), BUSAN_GEUMJEONGGU(Region.BUSAN, "금정구"), BUSAN_GANGSEOGU(Region.BUSAN, "강서구"),
    BUSAN_YEONJEGU(Region.BUSAN, "연제구"), BUSAN_SUYEONGGU(Region.BUSAN, "수영구"), BUSAN_SASANGGU(Region.BUSAN, "사상구"),
    BUSAN_GIJANG(Region.BUSAN, "기장군"),

    // 광주광역시
    GWANGJU_DONGGU(Region.GWANGJU, "동구"), GWANGJU_SEOGU(Region.GWANGJU, "서구"),
    GWANGJU_NAMGU(Region.GWANGJU, "남구"), GWANGJU_BUKGU(Region.GWANGJU, "북구"),
    GWANGJU_GWANGSAN(Region.GWANGJU, "광산구"),

    // 대전광역시
    DAEJEON_DONGGU(Region.DAEJEON, "동구"), DAEJEON_JUNGGU(Region.DAEJEON, "중구"), DAEJEON_SEOGU(Region.DAEJEON, "서구"),
    DAEJEON_YUSEONG(Region.DAEJEON, "유성구"), DAEJEON_DAEDEOK(Region.DAEJEON, "대덕구"),

    GANGWON_CHUNCHEON(Region.GANGWON, "춘천시"), GANGWON_WONJU(Region.GANGWON, "원주시"), GANGWON_GANGNEUNG(Region.GANGWON, "강릉시"),
    GANGWON_DONGHAE(Region.GANGWON, "동해시"), GANGWON_TAEBAEK(Region.GANGWON, "태백시"), GANGWON_SOKCHO(Region.GANGWON, "속초시"),
    GANGWON_SAMCHEOK(Region.GANGWON, "삼척시"), GANGWON_HONGCHEON(Region.GANGWON, "홍천군"), GANGWON_HOENGSEONG(Region.GANGWON, "횡성군"),
    GANGWON_YEONGWOL(Region.GANGWON, "영월군"), GANGWON_PYEONGCHANG(Region.GANGWON, "평창군"), GANGWON_JEONGSEON(Region.GANGWON, "정선군"),
    GANGWON_CHORWON(Region.GANGWON, "철원군"), GANGWON_HWACHEON(Region.GANGWON, "화천군"), GANGWON_YANGGU(Region.GANGWON, "양구군"),
    GANGWON_INJE(Region.GANGWON, "인제군"), GANGWON_GOSEONG(Region.GANGWON, "고성군"), GANGWON_YANGYANG(Region.GANGWON, "양양군"),

    CHUNGBUK_CHEONGJU(Region.CHUNGBUK, "청주시"), CHUNGBUK_CHUNGJU(Region.CHUNGBUK, "충주시"), CHUNGBUK_JECHEON(Region.CHUNGBUK, "제천시"),
    CHUNGBUK_BOEUN(Region.CHUNGBUK, "보은군"), CHUNGBUK_OKCHEON(Region.CHUNGBUK, "옥천군"), CHUNGBUK_YEONGDONG(Region.CHUNGBUK, "영동군"),
    CHUNGBUK_JEUNGPYEONG(Region.CHUNGBUK, "증평군"), CHUNGBUK_JINCHEON(Region.CHUNGBUK, "진천군"), CHUNGBUK_GOESAN(Region.CHUNGBUK, "괴산군"),
    CHUNGBUK_EUMSEONG(Region.CHUNGBUK, "음성군"), CHUNGBUK_DANYANG(Region.CHUNGBUK, "단양군"),

    CHUNGNAM_CHEONAN(Region.CHUNGNAM, "천안시"), CHUNGNAM_GONGJU(Region.CHUNGNAM, "공주시"), CHUNGNAM_BORYEONG(Region.CHUNGNAM, "보령시"),
    CHUNGNAM_ASAN(Region.CHUNGNAM, "아산시"), CHUNGNAM_SEOSAN(Region.CHUNGNAM, "서산시"), CHUNGNAM_NONSAN(Region.CHUNGNAM, "논산시"),
    CHUNGNAM_GYERYONG(Region.CHUNGNAM, "계룡시"), CHUNGNAM_DANGJIN(Region.CHUNGNAM, "당진시"), CHUNGNAM_GEUMSAN(Region.CHUNGNAM, "금산군"),
    CHUNGNAM_BUYEO(Region.CHUNGNAM, "부여군"), CHUNGNAM_SEOCHEON(Region.CHUNGNAM, "서천군"), CHUNGNAM_CHEONGYANG(Region.CHUNGNAM, "청양군"),
    CHUNGNAM_HONGSEONG(Region.CHUNGNAM, "홍성군"), CHUNGNAM_YESAN(Region.CHUNGNAM, "예산군"), CHUNGNAM_TAEAN(Region.CHUNGNAM, "태안군"),

    JEONBUK_JEONJU(Region.JEONBUK, "전주시"), JEONBUK_GUNSAN(Region.JEONBUK, "군산시"), JEONBUK_IKSAN(Region.JEONBUK, "익산시"),
    JEONBUK_JEOBUP(Region.JEONBUK, "정읍시"), JEONBUK_NAMWON(Region.JEONBUK, "남원시"), JEONBUK_GIMJE(Region.JEONBUK, "김제시"),
    JEONBUK_WANJU(Region.JEONBUK, "완주군"), JEONBUK_JINAN(Region.JEONBUK, "진안군"), JEONBUK_MUJU(Region.JEONBUK, "무주군"),
    JEONBUK_JANGSU(Region.JEONBUK, "장수군"), JEONBUK_IMSIL(Region.JEONBUK, "임실군"), JEONBUK_SUNCHANG(Region.JEONBUK, "순창군"),
    JEONBUK_GOCHANG(Region.JEONBUK, "고창군"), JEONBUK_BUAN(Region.JEONBUK, "부안군"),

    JEONNAM_MOKPO(Region.JEONNAM, "목포시"), JEONNAM_YEOSU(Region.JEONNAM, "여수시"), JEONNAM_SUNCHEON(Region.JEONNAM, "순천시"),
    JEONNAM_NAJU(Region.JEONNAM, "나주시"), JEONNAM_GWANGYANG(Region.JEONNAM, "광양시"), JEONNAM_DAMYANG(Region.JEONNAM, "담양군"),
    JEONNAM_GOKSEONG(Region.JEONNAM, "곡성군"), JEONNAM_GURYE(Region.JEONNAM, "구례군"), JEONNAM_GOHEUNG(Region.JEONNAM, "고흥군"),
    JEONNAM_BOSEONG(Region.JEONNAM, "보성군"), JEONNAM_HWASUN(Region.JEONNAM, "화순군"), JEONNAM_JANGHEUNG(Region.JEONNAM, "장흥군"),
    JEONNAM_GANGJIN(Region.JEONNAM, "강진군"), JEONNAM_HAENAM(Region.JEONNAM, "해남군"), JEONNAM_YEONGAM(Region.JEONNAM, "영암군"),
    JEONNAM_MUAN(Region.JEONNAM, "무안군"), JEONNAM_HAMPYEONG(Region.JEONNAM, "함평군"), JEONNAM_YEONGGWANG(Region.JEONNAM, "영광군"),
    JEONNAM_JANGSEONG(Region.JEONNAM, "장성군"), JEONNAM_WANDO(Region.JEONNAM, "완도군"), JEONNAM_JINDO(Region.JEONNAM, "진도군"),
    JEONNAM_SINAN(Region.JEONNAM, "신안군"),

    GYEONGBUK_POHANG(Region.GYEONGBUK, "포항시"), GYEONGBUK_GYEONGJU(Region.GYEONGBUK, "경주시"), GYEONGBUK_GIMCHEON(Region.GYEONGBUK, "김천시"),
    GYEONGBUK_ANDONG(Region.GYEONGBUK, "안동시"), GYEONGBUK_GUMI(Region.GYEONGBUK, "구미시"), GYEONGBUK_YEONGJU(Region.GYEONGBUK, "영주시"),
    GYEONGBUK_SANGJU(Region.GYEONGBUK, "상주시"), GYEONGBUK_MUNGYEONG(Region.GYEONGBUK, "문경시"), GYEONGBUK_GYEONGSAN(Region.GYEONGBUK, "경산시"),
    GYEONGBUK_UISONG(Region.GYEONGBUK, "의성군"), GYEONGBUK_CHEONGSONG(Region.GYEONGBUK, "청송군"), GYEONGBUK_YEONGYANG(Region.GYEONGBUK, "영양군"),
    GYEONGBUK_YEONGDEOK(Region.GYEONGBUK, "영덕군"), GYEONGBUK_CHONGDO(Region.GYEONGBUK, "청도군"), GYEONGBUK_GORYEONG(Region.GYEONGBUK, "고령군"),
    GYEONGBUK_SEONGJU(Region.GYEONGBUK, "성주군"), GYEONGBUK_CHILGOK(Region.GYEONGBUK, "칠곡군"), GYEONGBUK_YECHON(Region.GYEONGBUK, "예천군"),
    GYEONGBUK_BONGHWA(Region.GYEONGBUK, "봉화군"), GYEONGBUK_ULJIN(Region.GYEONGBUK, "울진군"), GYEONGBUK_ULLUNG(Region.GYEONGBUK, "울릉군"),

    GYEONGNAM_CHANGWON(Region.GYEONGNAM, "창원시"), GYEONGNAM_JINJU(Region.GYEONGNAM, "진주시"), GYEONGNAM_TONGYEONG(Region.GYEONGNAM, "통영시"),
    GYEONGNAM_SACHEON(Region.GYEONGNAM, "사천시"), GYEONGNAM_GIMHAE(Region.GYEONGNAM, "김해시"), GYEONGNAM_MIRYANG(Region.GYEONGNAM, "밀양시"),
    GYEONGNAM_GEOJE(Region.GYEONGNAM, "거제시"), GYEONGNAM_YANGSAN(Region.GYEONGNAM, "양산시"), GYEONGNAM_UIRYEONG(Region.GYEONGNAM, "의령군"),
    GYEONGNAM_HAMAN(Region.GYEONGNAM, "함안군"), GYEONGNAM_CHANGNYEONG(Region.GYEONGNAM, "창녕군"), GYEONGNAM_GOSEONG(Region.GYEONGNAM, "고성군"),
    GYEONGNAM_NAMHAE(Region.GYEONGNAM, "남해군"), GYEONGNAM_HADONG(Region.GYEONGNAM, "하동군"), GYEONGNAM_SANCHEONG(Region.GYEONGNAM, "산청군"),
    GYEONGNAM_HAMYANG(Region.GYEONGNAM, "함양군"), GYEONGNAM_GEOCHANG(Region.GYEONGNAM, "거창군"), GYEONGNAM_HAPCHEON(Region.GYEONGNAM, "합천군"),
    // 울산광역시
    ULSAN_JUNGGU(Region.ULSAN, "중구"), ULSAN_NAMGU(Region.ULSAN, "남구"),
    ULSAN_DONGGU(Region.ULSAN, "동구"), ULSAN_BUKGU(Region.ULSAN, "북구"), ULSAN_ULJU(Region.ULSAN, "울주군"),

    // 세종특별자치시 (단일)
    SEJONG_ALL(Region.SEJONG, "세종특별자치시"),

    JEJU_JEJU(Region.JEJU, "제주시"), JEJU_SEOGWIPO(Region.JEJU, "서귀포시");

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