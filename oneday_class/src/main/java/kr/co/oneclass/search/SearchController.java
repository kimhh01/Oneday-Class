package kr.co.oneclass.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.oneclass.main.ClassDTO;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService ss;

    /**
     * 통합 검색 미리보기 (클래스 + 작가)
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 1. 작가 및 클래스 미리보기 목록 조회 (최대 3~4개)
             List<CreatorDTO> creatorPreview = ss.searchCreatorPreview(keyword);
             List<ClassDTO> classPreview = ss.searchClassPreview(keyword);

            // 2. 전체 개수 조회
             int classCount = ss.searchClassCount(keyword);
             int creatorCount = ss.searchCreatorCount(keyword);

            model.addAttribute("creatorList", creatorPreview);
            model.addAttribute("classList", classPreview);
            model.addAttribute("classCount", classCount);
            model.addAttribute("creatorCount", creatorCount);
        }

        model.addAttribute("keyword", keyword);
        return "search/searchKeyword"; // 통합 검색 메인 View
    }

    /**
     * 작가 더보기 (페이징)
     */
    @GetMapping("/search/creator-more")
    public String searchCreatorMore(@RequestParam("keyword") String keyword,
                                    @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
                                    Model model) {
        int pageSize = 10;
        int startNum = (currentPage - 1) * pageSize + 1;
        int endNum = currentPage * pageSize;

        List<CreatorDTO> creatorList = ss.searchCreatorList(keyword, startNum, endNum);
        int totalCount = ss.searchCreatorCount(keyword);

        model.addAttribute("creatorList", creatorList); // 👈 추가
        model.addAttribute("totalCount", totalCount);  // 👈 추가
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("activeTab", "artist");    // 👈 작가 탭 활성화 indicator
        
        return "search/searchKeywordMore";
    }

    /**
     * 클래스 더보기 (페이징)
     */
    @GetMapping("/search/class-more")
    public String searchClassMore(@RequestParam("keyword") String keyword,
                                  @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
                                  Model model) {
        int pageSize = 12;
        int startNum = (currentPage - 1) * pageSize + 1;
        int endNum = currentPage * pageSize;

        List<ClassDTO> classList = ss.searchClassList(keyword, startNum, endNum);
        int totalCount = ss.searchClassCount(keyword);

        model.addAttribute("classList", classList);   // 👈 추가
        model.addAttribute("totalCount", totalCount);  // 👈 추가
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("activeTab", "class");     // 👈 클래스 탭 활성화 indicator
        
        return "search/searchKeywordMore";
    }
}