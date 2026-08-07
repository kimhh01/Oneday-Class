package kr.co.oneclass.bookmark;

import kr.co.oneclass.board.BoardUtil;
import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.ProfileService; // ProfileService 임포트
import kr.co.oneclass.board.RangeDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/mypage")
public class BookmarkController {

    @Autowired
    private BookmarkService bs;

    @Autowired
    private ProfileService ps; // 최신 프로필 정보 조회를 위해 주입 추가

    @GetMapping("/bookmark")
    public String bookmarkList(@RequestParam(value = "category", required = false, defaultValue = "ALL") String category,
                               @RequestParam(value = "nowPage", required = false, defaultValue = "1") int nowPage,
                               Model model, 
                               HttpSession session) {
        
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        int memberCode = loginMember.getMemberCode();

        // 1. [핵심] DB에서 최신 회원/프로필 정보 조회 (사이드바 프로필 이미지 표시용)
        Member member = ps.getProfile(String.valueOf(memberCode));
        model.addAttribute("member", member != null ? member : loginMember);

        // 2. 페이징 및 관심 클래스 목록 조회
        int pageScale = bs.pageScale();
        int startNum = bs.startNum(nowPage, pageScale);
        int endNum = bs.endNum(nowPage, pageScale);

        RangeDTO rDTO = RangeDTO.builder()
                .startNum(startNum)
                .endNum(endNum)
                .filed(category)
                .build();

        int totalCnt = bs.totalCnt(String.valueOf(memberCode), rDTO);
        List<Bookmark> bookmarkList = bs.getBookmarkList(memberCode, rDTO);

        String pagination = BoardUtil.pagination(totalCnt, nowPage, "category=" + category);

        model.addAttribute("bookmarkList", bookmarkList);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("totalCnt", totalCnt);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("pagination", pagination);

        return "bookmark/bookmark";
    }

    @PostMapping("/bookmark/toggle")
    @ResponseBody
    public String toggleBookmark(@RequestParam("classCode") String classCode, 
                                 HttpSession session) {
        
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "LOGIN_REQUIRED";
        }

        int memberCode = loginMember.getMemberCode();
        boolean isAdded = bs.toggleBookmark(memberCode, Integer.parseInt(classCode));
        return isAdded ? "ADDED" : "REMOVED";
    }
}