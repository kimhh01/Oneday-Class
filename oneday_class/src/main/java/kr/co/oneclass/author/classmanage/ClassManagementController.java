package kr.co.oneclass.author.classmanage;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.AuthorSessionUtils;

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
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        List<ClassManagementDTO> classes;
        try {
            classes = cmService.getClassManagementList(
                    authorCode, classStatus, keyword, fromDate, toDate);
        } catch (IllegalArgumentException exception) {
            classes = List.of();
            model.addAttribute("managementError", exception.getMessage());
        }
        ClassManagementSummaryDTO summary = cmService.getClassManagementSummary(authorCode);

        model.addAttribute("classes", classes);
        model.addAttribute("classStatus", classStatus);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("summary", summary);
        return "author/class-manage";
    }

    // 클래스 상세정보와 일정별 모집 인원 현황을 출력한다
    @GetMapping("/author/classes/{classCode}")
    public String classManagementDetail(
            @PathVariable("classCode") int classCode,
            Model model,
            HttpSession session) {

        ClassManagementDetailDTO detail = cmService.getClassManagementDetail(
                AuthorSessionUtils.getAuthorCode(session), classCode);
        if (detail == null) {
            return "redirect:/author/classes";
        }
        model.addAttribute("classDetail", detail);
        return "author/class-manage-detail";
    }

    // 승인된 내 클래스의 일정을 한 화면에서 검색하고 관리한다
    @GetMapping("/author/schedules")
    public String scheduleManagement(
            @RequestParam(value = "scheduleStatus", required = false, defaultValue = "all")
            String scheduleStatus,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        List<ScheduleManageDTO> schedules;
        try {
            schedules = cmService.getAuthorScheduleList(
                    authorCode, scheduleStatus, keyword, fromDate, toDate);
        } catch (IllegalArgumentException exception) {
            schedules = List.of();
            model.addAttribute("managementError", exception.getMessage());
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("summary", cmService.summarizeSchedules(schedules));
        model.addAttribute("scheduleStatus", scheduleStatus);
        model.addAttribute("keyword", keyword);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "author/schedule-manage";
    }

    // 승인 완료 또는 비공개 클래스를 공개 상태로 변경한다
    @PostMapping("/author/classes/{classCode}/open")
    public String openClass(
            @PathVariable("classCode") int classCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!cmService.openClass(AuthorSessionUtils.getAuthorCode(session), classCode)) {
            redirectAttributes.addFlashAttribute("managementError", "공개할 수 있는 클래스가 아닙니다.");
        } else {
            redirectAttributes.addFlashAttribute("managementMessage", "클래스를 공개했습니다.");
        }
        return "redirect:/author/classes/" + classCode;
    }

    // 공개 중인 클래스를 비공개 상태로 변경한다
    @PostMapping("/author/classes/{classCode}/hide")
    public String hideClass(
            @PathVariable("classCode") int classCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (!cmService.hideClass(AuthorSessionUtils.getAuthorCode(session), classCode)) {
            redirectAttributes.addFlashAttribute("managementError", "비공개로 전환할 수 있는 클래스가 아닙니다.");
        } else {
            redirectAttributes.addFlashAttribute("managementMessage", "클래스를 비공개로 전환했습니다.");
        }
        return "redirect:/author/classes/" + classCode;
    }

    // 선택한 일정의 남은 모집 인원을 변경한다
    @PostMapping("/author/classes/schedules/{scheduleCode}/people")
    public String modifySchedulePeople(
            @PathVariable("scheduleCode") int scheduleCode,
            @RequestParam("classCode") int classCode,
            @RequestParam("remainingPeople") int remainingPeople,
            @RequestParam(value = "source", required = false) String source,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            cmService.modifySchedulePeople(
                    AuthorSessionUtils.getAuthorCode(session), scheduleCode, remainingPeople);
            redirectAttributes.addFlashAttribute("managementMessage", "일정의 남은 자리를 변경했습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("managementError", exception.getMessage());
        }
        return scheduleRedirect(source, classCode);
    }

    // 선택한 일정을 마감하고 예약·결제를 취소·환불 상태로 변경한다
    @PostMapping("/author/classes/schedules/{scheduleCode}/cancel")
    public String cancelSchedule(
            @PathVariable("scheduleCode") int scheduleCode,
            @RequestParam("classCode") int classCode,
            @RequestParam(value = "source", required = false) String source,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            int canceledReservationCount = cmService.cancelSchedule(
                    AuthorSessionUtils.getAuthorCode(session), classCode, scheduleCode);
            String message = canceledReservationCount > 0
                    ? "일정을 마감하고 예약 " + canceledReservationCount
                            + "건을 취소·환불 처리했습니다. 예약자 카카오톡 안내를 별도로 진행해주세요."
                    : "일정을 모집 마감했습니다. 예약자는 없습니다.";
            redirectAttributes.addFlashAttribute("managementMessage", message);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("managementError", exception.getMessage());
        }
        return scheduleRedirect(source, classCode);
    }

    // 비밀번호와 운영 조건을 확인하고 클래스를 폐강한다
    @PostMapping("/author/classes/{classCode}/close")
    public String closeClass(
            @PathVariable("classCode") int classCode,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            cmService.closeClass(AuthorSessionUtils.getAuthorCode(session), classCode, password);
            redirectAttributes.addFlashAttribute("managementMessage", "클래스를 폐강했습니다.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("managementError", exception.getMessage());
        }
        return "redirect:/author/classes/" + classCode;
    }

    private String scheduleRedirect(String source, int classCode) {
        return "schedules".equals(source)
                ? "redirect:/author/schedules"
                : "redirect:/author/classes/" + classCode;
    }
}
