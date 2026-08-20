package kr.co.oneclass.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {
	
    // @RequiredArgsConstructor가 생성자 주입을 처리하므로 @Autowired 제거
    private final MainDAO mDAO;

    // 대표 이미지 세팅 로직
    private void setMainImages(List<ClassDTO> classList) {
        if (classList == null) return;
        
        for (ClassDTO dto : classList) {
            // [수정] MainDAO -> mDAO (주입받은 변수 사용)
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
        return mDAO.selectTopRatedClass(categoryCode, limitCount);
    }
    
    public List<ClassDTO> searchTopRatedClassList(int categoryCode) {
        return mDAO.selectTopRatedClassList(categoryCode);
    }

    public List<ClassDTO> searchTodayClass() {
        return mDAO.selectTodayClass();
    }

    public List<ClassDTO> searchWeekendClass() {
        return mDAO.selectWeekendClass();
    }

    public List<ClassDTO> searchAvailableClass(int categoryCode, ScheduleDTO scheduleDTO) {
        return mDAO.selectAvailableClass(categoryCode, scheduleDTO);
    }

    public List<ClassImageDTO> searchImage(int classCode) {
        return mDAO.selectImage(classCode);
    }
}