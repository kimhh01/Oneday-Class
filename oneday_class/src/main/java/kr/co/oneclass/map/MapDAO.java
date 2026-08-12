package kr.co.oneclass.map;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import kr.co.oneclass.common.ClassDTO;

@Mapper
public interface MapDAO {
    // 통합 조회 메서드 하나로 모든 조건(동, 내위치, 카테고리, 영역) 처리
    List<ClassDTO> selectClassList(MapSearchDTO searchDTO);
    
    ClassDTO selectClassDetail(int classCode);
}