package kr.co.oneclass.map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MapSearchDTO {
    // 1. 동, 카테고리, 키워드 검색
    private String dong;      // 동 이름 (예: "역삼동")
    private String sido;      // 시/도
    private String sigungu;   // 구/군
    private String category;  // 카테고리명
    private String keyword;   // 검색어

    // 2. 내 위치(위경도) 및 반경 검색
    private Double lat;       // 기준 위도
    private Double lng;       // 기준 경도
    private Double distance = 3.0; // 기본 반경 3km

    // 3. 지도 영역(Bounds) 범위 검색
    private Double minLat;
    private Double maxLat;
    private Double minLng;
    private Double maxLng;
}