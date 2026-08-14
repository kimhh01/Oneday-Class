package kr.co.oneclass.author.classbasic;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.common.AuthorSessionUtils;

@Controller
public class ClassController {

    private final ClassService cService;
    private final String kakaoMapAppKey;

    public ClassController(ClassService cService,
            @Value("${oneday.maps.kakao-app-key:}") String kakaoMapAppKey) {
        this.cService = cService;
        this.kakaoMapAppKey = kakaoMapAppKey;
    }

    // 클래스 등록 시작 안내
    @GetMapping("/author/classes/register-guide")
    public String classRegisterGuide(Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        ClassBasicDTO draft = cService.getLatestDraftClass(authorCode);
        if (draft != null) {
            model.addAttribute("latestDraft", draft);
            model.addAttribute("draftResumeUrl",
                    cService.getDraftResumePath(authorCode, draft.getClassCode()));
        }
        return "author/class-register";
    }

    // 클래스 등록 시작 버튼
    @PostMapping("/author/classes/register/start")
    public String startClassRegister(HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        int classCode = cService.addDraftClass(authorCode);
        return "redirect:" + cService.getDraftResumePath(authorCode, classCode);
    }

    @PostMapping("/author/classes/register/restart")
    public String restartClassRegister(
            @RequestParam("classCode") int classCode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        try {
            int newClassCode = cService.replaceDraftClass(authorCode, classCode);
            return "redirect:/author/classes/register/basic?classCode=" + newClassCode;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register-guide";
        }
    }

    // 클래스명·카테고리·소개·대표사진 입력 화면
    @GetMapping("/author/classes/register/basic")
    public String classBasicFrm(
            @RequestParam(value = "classCode", required = false) Integer classCode,
            Model model, HttpSession session) {

        if (classCode == null) {
            return "redirect:/author/classes/register-guide";
        }
        int code = classCode;
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        ClassBasicDTO basic = cService.getClassBasic(authorCode, code);
        if (basic == null) {
            return "redirect:/author/classes/register-guide";
        }
        cService.markDraftStep(authorCode, code, "basic");
        model.addAttribute("basicForm", basic);
        model.addAttribute("categories", cService.getCategories());
        model.addAttribute("editClassCode", null);
        return "author/class-register-basic";
    }

    // 기본정보 저장 및 다음 단계 이동
    @PostMapping("/author/classes/register/basic")
    public String saveClassBasic(
            ClassBasicDTO cbDTO,
            @RequestParam(value = "mainFiles", required = false) List<MultipartFile> mainFiles,
            @RequestParam(value = "saveMode", required = false, defaultValue = "next") String saveMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        cbDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            cService.modifyClassBasic(cbDTO, mainFiles);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "기본 정보를 임시 저장했습니다.");
                return "redirect:/author/classes/register/basic?classCode=" + cbDTO.getClassCode();
            }
            return "redirect:/author/classes/register/location?classCode=" + cbDTO.getClassCode();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register/basic?classCode=" + cbDTO.getClassCode();
        }
    }

    // 클래스 주소 및 지도 설정 화면
    @GetMapping("/author/classes/register/location")
    public String classLocationFrm(
            @RequestParam("classCode") int classCode,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        ClassLocationDTO location = cService.getClassLocation(authorCode, classCode);
        if (location == null) {
            return "redirect:/author/classes/register-guide";
        }
        cService.markDraftStep(authorCode, classCode, "location");
        model.addAttribute("locationForm", location);
        model.addAttribute("kakaoMapAppKey", kakaoMapAppKey);
        return "author/class-register-location";
    }

    // 클래스 주소 및 지도 정보 저장
    @PostMapping("/author/classes/register/location")
    public String saveClassLocation(
            ClassLocationDTO clDTO,
            @RequestParam(value = "saveMode", required = false, defaultValue = "next") String saveMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        clDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            cService.modifyClassLocation(clDTO);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "장소 정보를 임시 저장했습니다.");
                return "redirect:/author/classes/register/location?classCode=" + clDTO.getClassCode();
            }
            return "redirect:/author/classes/register/schedule?classCode=" + clDTO.getClassCode();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register/location?classCode=" + clDTO.getClassCode();
        }
    }

    // 클래스 일정 및 가격 입력 화면
    @GetMapping("/author/classes/register/schedule")
    public String classScheduleFrm(
            @RequestParam("classCode") int classCode,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        ClassScheduleDTO schedule = cService.getClassSchedule(authorCode, classCode);
        if (schedule == null) {
            return "redirect:/author/classes/register-guide";
        }
        cService.markDraftStep(authorCode, classCode, "schedule");
        model.addAttribute("scheduleForm", schedule);
        return "author/class-register-schedule";
    }

    // 클래스 일정 및 가격 저장
    @PostMapping("/author/classes/register/schedule")
    public String saveClassSchedule(
            ClassScheduleDTO csDTO,
            @RequestParam(value = "saveMode", required = false, defaultValue = "next") String saveMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        csDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            cService.modifyClassSchedule(csDTO);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "일정과 가격을 임시 저장했습니다.");
                return "redirect:/author/classes/register/schedule?classCode=" + csDTO.getClassCode();
            }
            return "redirect:/author/classes/register/detail?classCode=" + csDTO.getClassCode();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register/schedule?classCode=" + csDTO.getClassCode();
        }
    }

    // 클래스 상세정보 입력 화면
    @GetMapping("/author/classes/register/detail")
    public String classDetailFrm(
            @RequestParam("classCode") int classCode,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        ClassDetailDTO detail = cService.getClassDetail(authorCode, classCode);
        if (detail == null) {
            return "redirect:/author/classes/register-guide";
        }
        cService.markDraftStep(authorCode, classCode, "detail");
        model.addAttribute("detailForm", detail);
        return "author/class-register-detail";
    }

    // 클래스 상세정보 1/2와 완성작 이미지 저장
    @PostMapping("/author/classes/register/detail")
    public String saveClassDetail(
            ClassDetailDTO cdDTO,
            @RequestParam(value = "resultFiles", required = false) List<MultipartFile> resultFiles,
            @RequestParam(value = "saveMode", required = false, defaultValue = "next") String saveMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        cdDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            cService.modifyClassDetail(cdDTO, resultFiles);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "상세 정보 1/2를 임시 저장했습니다.");
                return "redirect:/author/classes/register/detail?classCode=" + cdDTO.getClassCode();
            }
            return "redirect:/author/classes/register/detail-extra?classCode=" + cdDTO.getClassCode();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register/detail?classCode=" + cdDTO.getClassCode();
        }
    }

    // 클래스 상세정보 2/2 입력 화면
    @GetMapping("/author/classes/register/detail-extra")
    public String classDetailExtraFrm(
            @RequestParam("classCode") int classCode,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        ClassDetailDTO detail = cService.getClassDetail(authorCode, classCode);
        if (detail == null) {
            return "redirect:/author/classes/register-guide";
        }
        cService.markDraftStep(authorCode, classCode, "detail-extra");
        model.addAttribute("detailForm", detail);
        model.addAttribute("offerings", cService.getOfferings());
        return "author/class-register-detail-extra";
    }

    // 클래스 상세정보 2/2와 작품 갤러리 이미지 저장
    @PostMapping("/author/classes/register/detail-extra")
    public String saveClassDetailExtra(
            ClassDetailDTO cdDTO,
            @RequestParam(value = "galleryFiles", required = false) List<MultipartFile> galleryFiles,
            @RequestParam(value = "saveMode", required = false, defaultValue = "next") String saveMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        cdDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            cService.modifyClassDetailExtra(cdDTO, galleryFiles);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "상세 정보 2/2를 임시 저장했습니다.");
                return "redirect:/author/classes/register/detail-extra?classCode=" + cdDTO.getClassCode();
            }
            return "redirect:/author/classes/register/curriculum?classCode=" + cdDTO.getClassCode();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register/detail-extra?classCode=" + cdDTO.getClassCode();
        }
    }

    // 클래스 커리큘럼 입력 화면
    @GetMapping("/author/classes/register/curriculum")
    public String classCurriculumFrm(
            @RequestParam("classCode") int classCode,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        if (cService.getClassBasic(authorCode, classCode) == null) {
            return "redirect:/author/classes/register-guide";
        }
        cService.markDraftStep(authorCode, classCode, "curriculum");
        model.addAttribute("curriculumForm", cService.getClassCurriculum(authorCode, classCode));
        return "author/class-register-curriculum";
    }

    // 클래스 커리큘럼 전체 저장
    @PostMapping("/author/classes/register/curriculum")
    public String saveClassCurriculum(
            CurriculumFormDTO crDTO,
            @RequestParam(value = "saveMode", required = false, defaultValue = "next") String saveMode,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        crDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            cService.modifyClassCurriculum(crDTO);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "커리큘럼을 임시 저장했습니다.");
                return "redirect:/author/classes/register/curriculum?classCode=" + crDTO.getClassCode();
            }
            return "redirect:/author/classes/register/preview?classCode=" + crDTO.getClassCode();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register/curriculum?classCode=" + crDTO.getClassCode();
        }
    }

    // 클래스 등록 내용 미리보기
    @GetMapping("/author/classes/register/preview")
    public String classPreview(
            @RequestParam("classCode") int classCode,
            Model model,
            HttpSession session) {

        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        ClassPreviewDTO preview = cService.getClassPreview(authorCode, classCode);
        if (preview == null) {
            return "redirect:/author/classes/register-guide";
        }
        cService.markDraftStep(authorCode, classCode, "preview");
        model.addAttribute("preview", preview);
        return "author/class-register-preview";
    }

    // 클래스 등록 신청 버튼
    @PostMapping("/author/classes/register/submit")
    public String submitClass(
            ClassSubmitDTO csDTO,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        csDTO.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            cService.submitClass(csDTO);
            return "redirect:/author/classes/register/complete?classCode=" + csDTO.getClassCode();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return "redirect:/author/classes/register/preview?classCode=" + csDTO.getClassCode();
        }
    }

    // 클래스 등록 완료 화면
    @GetMapping("/author/classes/register/complete")
    public String classRegisterComplete(
            @RequestParam("classCode") int classCode,
            Model model,
            HttpSession session) {

        ClassRegisterResultDTO result = cService.getRegisterResult(
                AuthorSessionUtils.getAuthorCode(session), classCode);
        if (result == null) {
            return "redirect:/author/classes/register-guide";
        }
        model.addAttribute("classResult", result);
        return "author/class-register-complete";
    }

    private boolean isStay(String saveMode) {
        return "stay".equalsIgnoreCase(saveMode);
    }
}
