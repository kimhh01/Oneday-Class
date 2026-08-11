package kr.co.oneclass.author.classmanage.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.util.AuthorSessionUtils;
import kr.co.oneclass.author.classmanage.dto.ClassManagementDTO;
import kr.co.oneclass.author.classmanage.service.ClassManagementService;

@Controller
public class ClassManagementController {

    private final ClassManagementService cmService;

    public ClassManagementController(ClassManagementService cmService) {
        this.cmService = cmService;
    }

    // 승인된 클래스 목록을 운영 상태·일정 유형·검색어 기준으로 조회한다
    @GetMapping("/author/classes")
    public String classManagementList(
            @RequestParam(value = "classStatus", required = false, defaultValue = "all") String classStatus,
            @RequestParam(value = "scheduleType", required = false) String scheduleType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "duration", required = false) String duration,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        List<ClassManagementDTO> classes =
                cmService.getClassManagementList(authorCode, classStatus, scheduleType, keyword);

        model.addAttribute("classes", classes);
        model.addAttribute("classStatus", classStatus);
        model.addAttribute("scheduleType", scheduleType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);

        // TODO: Mapper 연결 후 제거 - 요약 지표는 DB 집계로 교체
        // CLASS.STATUS 는 한글이다. 공개 상태에 해당하는 실측값은 '모집중'
        model.addAttribute("openClassCount",
                classes.stream().filter(item -> "모집중".equals(item.getClassStatus())).count());
        model.addAttribute("monthlyScheduleCount",
                classes.stream().mapToInt(ClassManagementDTO::getUpcomingScheduleCount).sum());
        model.addAttribute("totalApplicantCount",
                classes.stream().mapToInt(ClassManagementDTO::getApplicantCount).sum());
        return "author/class-manage";
    }

    // 클래스 상세정보와 일정별 모집 인원 현황을 출력한다
    @GetMapping("/author/classes/{classCode}")
    public String classManagementDetail(
            @PathVariable("classCode") int classCode,
            Model model,
            HttpSession session) {

        model.addAttribute("classDetail",
                cmService.getClassManagementDetail(AuthorSessionUtils.getAuthorCode(session), classCode));
        return "author/class-manage-detail";
    }

    // 승인 완료 또는 비공개 클래스를 공개 상태로 변경한다
    @PostMapping("/author/classes/{classCode}/open")
    public String openClass(
            @PathVariable("classCode") int classCode,
            HttpSession session) {

        cmService.openClass(AuthorSessionUtils.getAuthorCode(session), classCode);
        return "redirect:/author/classes/" + classCode;
    }

    // 공개 중인 클래스를 비공개 상태로 변경한다
    @PostMapping("/author/classes/{classCode}/hide")
    public String hideClass(
            @PathVariable("classCode") int classCode,
            HttpSession session) {

        cmService.hideClass(AuthorSessionUtils.getAuthorCode(session), classCode);
        return "redirect:/author/classes/" + classCode;
    }

    // 선택한 일정의 남은 모집 인원을 변경한다
    @PostMapping("/author/classes/schedules/{scheduleCode}/people")
    public String modifySchedulePeople(
            @PathVariable("scheduleCode") int scheduleCode,
            @RequestParam("remainingPeople") int remainingPeople,
            HttpSession session) {

        cmService.modifySchedulePeople(AuthorSessionUtils.getAuthorCode(session), scheduleCode, remainingPeople);
        return "redirect:/author/classes";
    }

    // 비밀번호와 운영 조건을 확인하고 클래스를 폐쇄한다
    @PostMapping("/author/classes/{classCode}/close")
    public String closeClass(
            @PathVariable("classCode") int classCode,
            @RequestParam("password") String password,
            HttpSession session) {

        cmService.closeClass(AuthorSessionUtils.getAuthorCode(session), classCode, password);
        return "redirect:/author/classes/" + classCode;
    }
}
