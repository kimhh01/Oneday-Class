package kr.co.oneclass.map;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("/map")
    public String showMapPage(@ModelAttribute MapSearchDTO searchDTO, Model model) {
        // 서비스 구현 메서드로 연결
        List<ClassDTO> classList = mapService.getClassList(searchDTO);
        List<CategoryDTO> categoryList= mapService.getCategoryList();
        
        model.addAttribute("classList", classList);
        model.addAttribute("categoryList", categoryList);
        return "map/map"; // map.html 호출 (src/main/resources/templates/map.html 기준)
    }
}