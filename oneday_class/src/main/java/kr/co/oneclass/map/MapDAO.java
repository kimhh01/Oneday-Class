package kr.co.oneclass.map;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.oneclass.common.ClassDTO;

@Mapper
public interface MapDAO {
	
	// 💡 1. 메인 지도 화면/사이드바에 뿌려줄 클래스 전체(또는 검색) 목록 조회 메서드 추가!
    public List<ClassDTO> selectClassList(MapSearchDTO searchDTO);
	
	public List<MapSearchDTO> search(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng);
	
}
