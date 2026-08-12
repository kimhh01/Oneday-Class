package kr.co.oneclass.common;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryDAO {
	
	public List<CategoryDTO> selectCategoryList();
	
	
	public List<ClassImageDTO> selectImage(int classCode);
	
}		
