package kr.co.oneclass.map;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {
	
	@Autowired
	private final MapDAO mDAO;
	
	public List<ClassDTO> getClassList(MapSearchDTO mDTO) {
		
		List<ClassDTO> classList=mDAO.selectClassList(mDTO);
		
		for(ClassDTO cDTO : classList) {
			if(!"모집중".equals(cDTO.getStatus())) {
				return null;
			}
		}
		return mDAO.selectClassList(mDTO); 
	}
	
	
	public List<CategoryDTO> getCategoryList() {
		return mDAO.selectCategoryList();
	}
	
}