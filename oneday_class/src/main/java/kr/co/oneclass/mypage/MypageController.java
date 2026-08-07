package kr.co.oneclass.mypage;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.notice.NoticeDoamin;
import kr.co.oneclass.profile.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    @Autowired
    private MypageService mps;

    @Autowired
    private ProfileService pfs; // 최신 프로필 정보 조회를 위한 서비스 주입

    @GetMapping
    public String mypage(Model model, HttpSession session) {
        // 1. 세션에서 로그인 회원 정보 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 비로그인 시 로그인 페이지로 이동
        if (loginMember == null) {
            return "redirect:/member/login";
        }

        // 2. DB에서 최신 회원 프로필 정보 조회 (프로필 이미지 갱신 보장)
        Member member = pfs.getProfile(String.valueOf(loginMember.getMemberCode()));

        // 3. 가장 최근 공지사항 1건 DB 조회
        NoticeDoamin recentNotice = mps.getRecentNotice();

        // 4. View로 데이터 전송 (DB 조회가 성공하면 member, 실패 시 loginMember 전송)
        model.addAttribute("member", member != null ? member : loginMember);
        model.addAttribute("recentNotice", recentNotice);

        return "mypage/mypage";
    }
}