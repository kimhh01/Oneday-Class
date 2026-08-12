package kr.co.oneclass.map;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /**
     * 1. 맵 페이지 화면 렌더링 (Thymeleaf)
     */
    @GetMapping("/map")
    public String showMapPage(@ModelAttribute MapSearchDTO searchDTO, Model model) {
        // 초기 로드 시 클래스 목록 및 카테고리 목록 전달
        List<ClassDTO> classList = mapService.getClassList(searchDTO);
        List<CategoryDTO> categoryList = mapService.getCategoryList();
        
        model.addAttribute("classList", classList);
        model.addAttribute("categoryList", categoryList);
        
        return "map/map"; // templates/map/map.html
    }

    /**
     * 2. 지도 이동 / 카테고리 변경 시 AJAX 호출용 REST API (JSON 반환)
     * Front-end JS: fetch(`/api/map/classes?dong=역삼동&category=베이킹`)
     */
    @GetMapping("/api/map/classes")
    @ResponseBody
    public ResponseEntity<List<ClassDTO>> getClassesApi(@ModelAttribute MapSearchDTO searchDTO) {
        // searchDTO 필드로 dong, category, keyword 등이 수신됩니다.
        List<ClassDTO> classList = mapService.getClassList(searchDTO);
        return ResponseEntity.ok(classList);
    }
}