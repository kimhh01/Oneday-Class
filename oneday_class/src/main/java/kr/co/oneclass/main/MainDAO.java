package kr.co.oneclass.main;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.oneclass.category.CategoryDTO;

import java.util.List;

@Mapper
public interface MainDAO {
    // limitCount 파라미터 추가
    List<ClassDTO> selectTopRatedClass(@Param("categoryCode") long categoryCode, @Param("limitCount") int limitCount);
    List<ClassDTO> selectTopRatedClassList(long categoryCode);
    List<ClassDTO> selectTodayClass();
    List<ClassDTO> selectWeekendClass();
    List<ClassDTO> selectAvailableClass(@Param("categoryCode") long categoryCode, @Param("scheduleDTO") ScheduleDTO scheduleDTO);
    List<ClassImageDTO> selectImage(long classCode);
	List<CategoryDTO> selectCategoryList();
	int selectRegionClassCount(@Param("keyword") String keyword);
}