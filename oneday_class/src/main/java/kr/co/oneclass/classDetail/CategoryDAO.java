package kr.co.oneclass.classDetail;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassImageDTO;

@Mapper
public interface CategoryDAO {
	
	public List<CategoryDTO> selectCategoryList();
	
	
	public List<ClassImageDTO> selectImage(int classCode);
	
}		
