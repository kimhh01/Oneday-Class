package kr.co.oneclass.notice;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.ProfileService;
import kr.co.oneclass.board.BoardUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/mypage")
public class NoticeController {

    @Autowired
    private NoticeService ns;

    @Autowired
    private ProfileService ps;

    /**
     * 1. 공지사항 목록 조회
     */
    @GetMapping("/notice")
    public String noticeList(@RequestParam(value = "category", required = false, defaultValue = "ALL") String type,
                             @RequestParam(value = "currentPage", required = false, defaultValue = "1") int currentPage,
                             Model model,
                             HttpSession session) {

        // 💡 [핵심] 비로그인 사용자 접근 차단 및 로그인 페이지 이동
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 사이드바 프로필 데이터 조회
        Member member = ps.getProfile(String.valueOf(loginMember.getMemberCode()));
        model.addAttribute("member", member != null ? member : loginMember);

        // 페이지네이션 범위 계산
        int pageScale = ns.pageScale();
        int startNum = ns.startNum(currentPage, pageScale);
        int endNum = ns.endNum(currentPage, pageScale);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("category", type);
        paramMap.put("startNum", startNum);
        paramMap.put("endNum", endNum);

        // 목록 데이터 및 총 개수 조회
        int totalCnt = ns.totalCnt(paramMap);
        List<NoticeDTO> noticeList = ns.getNoticeList(paramMap);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("selectedCategory", type);

        // BoardUtil을 통한 하단 페이지네이션 생성
        int totalPage = ns.totalPage(totalCnt, pageScale);
        String pagination = BoardUtil.pagination(currentPage, totalPage, "/mypage/notice?category=" + type);
        model.addAttribute("pagination", pagination);

        return "notice/notice";
    }

    /**
     * 2. 공지사항 상세 조회
     */
    @GetMapping("/notice/detail")
    public String noticeDetail(@RequestParam("noticeCode") String noticeCode,
                               Model model,
                               HttpSession session) {

        // 💡 [핵심] 비로그인 사용자 접근 차단 및 로그인 페이지 이동
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 사이드바 프로필 데이터 조회
        Member member = ps.getProfile(String.valueOf(loginMember.getMemberCode()));
        model.addAttribute("member", member != null ? member : loginMember);

        NoticeDTO notice = ns.getNoticeDetail(noticeCode);
        model.addAttribute("notice", notice);

        return "notice/notice_detail";
    }
}