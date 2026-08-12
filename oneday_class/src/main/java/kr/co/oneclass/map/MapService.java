package kr.co.oneclass.map;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.oneclass.classDetail.CategoryDAO;
import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {
	
	private final MapDAO mDAO;
	private final CategoryDAO cDAO;
	
	public List<ClassDTO> getClassList(MapSearchDTO mDTO) {
		return mDAO.selectClassList(mDTO); 
	}
	
	
	public List<CategoryDTO> getCategoryList() {
		return cDAO.selectCategoryList();
	}
	
	
	public List<MapSearchDTO> search(double minLat, double maxLat, double minLng, double maxLng) {
		return mDAO.search(minLat, maxLat, minLng, maxLng);
	}
}