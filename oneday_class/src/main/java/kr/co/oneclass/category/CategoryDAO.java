package kr.co.oneclass.category;

import kr.co.oneclass.main.ClassDTO;

import kr.co.oneclass.main.ClassImageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryDAO {

    // 대분류 및 소분류 카테고리 목록 조회
    List<CategoryDTO> selectCategoryList();

    // 검색 조건에 맞는 카테고리별 클래스 목록 조회
    List<ClassDTO> selectCategoryClass(CategorySearchDTO searchDTO);

    // 클래스 이미지 목록 조회
    List<ClassImageDTO> selectImage(long classCode);
}