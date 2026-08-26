package kr.co.oneclass.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import kr.co.oneclass.category.CategoryDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

    private final MainDAO mDAO;

    // 대표 이미지 세팅 로직
    private void setMainImages(List<ClassDTO> classList) {
        if (classList == null || classList.isEmpty()) return;
        
        for (ClassDTO dto : classList) {
            List<ClassImageDTO> imgList = mDAO.selectImage(dto.getClassCode());
            
            if (imgList != null && !imgList.isEmpty()) {
                // 1. TYPE이 '대표'인 이미지 우선 탐색
                String mainImgPath = imgList.stream()
                        .filter(img -> "대표".equals(img.getType()))
                        .map(ClassImageDTO::getImage)
                        .findFirst()
                        .orElse(imgList.get(0).getImage()); // 없으면 첫번째 이미지
                
                dto.setMainImage(mainImgPath);
            } else {
                // 이미지가 없는 경우 디폴트 이미지
                dto.setMainImage("/images/default-class.jpg");
            }
        }
    }

    public List<ClassDTO> searchTopRatedClass(int categoryCode, int limitCount) {
        List<ClassDTO> list = mDAO.selectTopRatedClass(categoryCode, limitCount);
        setMainImages(list); 
        return list;
    }
    
    public List<ClassDTO> searchTopRatedClassList(int categoryCode) {
        List<ClassDTO> list = mDAO.selectTopRatedClassList(categoryCode);
        setMainImages(list);
        return list;
    }

    public List<ClassDTO> searchTodayClass() {
        List<ClassDTO> list = mDAO.selectTodayClass();
        setMainImages(list); 
        return list;
    }

    public List<ClassDTO> searchWeekendClass() {
        List<ClassDTO> list = mDAO.selectWeekendClass();
        setMainImages(list); 
        return list;
    }

    public List<ClassDTO> searchAvailableClass(int categoryCode, ScheduleDTO scheduleDTO) {
        List<ClassDTO> list = mDAO.selectAvailableClass(categoryCode, scheduleDTO);
        setMainImages(list); 
        return list;
    }

    public List<ClassImageDTO> searchImage(long classCode) { // 👈 int -> long 수정 (DTO 타입 통일)
        return mDAO.selectImage(classCode);
    }
    
    public List<CategoryDTO> getCategoryList() {
        return mDAO.selectCategoryList(); // 또는 categoryDAO.selectCategoryList()
    }
}