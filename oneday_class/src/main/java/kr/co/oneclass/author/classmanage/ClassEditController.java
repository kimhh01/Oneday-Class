package kr.co.oneclass.author.classmanage;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.co.oneclass.author.classbasic.ClassBasicDTO;
import kr.co.oneclass.author.classbasic.ClassDetailDTO;
import kr.co.oneclass.author.classbasic.ClassLocationDTO;
import kr.co.oneclass.author.classbasic.ClassScheduleDTO;
import kr.co.oneclass.author.classbasic.CurriculumFormDTO;
import kr.co.oneclass.author.classbasic.ClassService;
import kr.co.oneclass.author.common.AuthorSessionUtils;

@Controller
public class ClassEditController {

    private final ClassService classService;
    private final String kakaoMapAppKey;

    public ClassEditController(ClassService classService,
            @Value("${oneday.maps.kakao-app-key:}") String kakaoMapAppKey) {
        this.classService = classService;
        this.kakaoMapAppKey = kakaoMapAppKey;
    }

    @GetMapping("/author/classes/{classCode}/edit/basic")
    public String basicForm(@PathVariable("classCode") int classCode,
            Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        if (!classService.isApprovedClassEditable(authorCode, classCode)) {
            return classRedirect(classCode);
        }
        model.addAttribute("basicForm", classService.getClassBasic(authorCode, classCode));
        model.addAttribute("categories", classService.getCategories());
        addEditModel(model, classCode, "basic");
        return "author/class-register-basic";
    }

    @PostMapping("/author/classes/{classCode}/edit/basic")
    public String saveBasic(@PathVariable("classCode") int classCode,
            ClassBasicDTO form,
            @RequestParam(value = "mainFiles", required = false) List<MultipartFile> mainFiles,
            @RequestParam(value = "saveMode", defaultValue = "next") String saveMode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        form.setClassCode(classCode);
        form.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            classService.modifyApprovedClassBasic(form, mainFiles);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "기본 정보를 저장했습니다.");
                return editRedirect(classCode, "basic");
            }
            return editRedirect(classCode, "pricing");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return editRedirect(classCode, "basic");
        }
    }

    @GetMapping("/author/classes/{classCode}/edit/pricing")
    public String pricingForm(@PathVariable("classCode") int classCode,
            Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        if (!classService.isApprovedClassEditable(authorCode, classCode)) {
            return classRedirect(classCode);
        }
        model.addAttribute("pricingForm", classService.getClassSchedule(authorCode, classCode));
        addEditModel(model, classCode, "pricing");
        return "author/class-edit-pricing";
    }

    @PostMapping("/author/classes/{classCode}/edit/pricing")
    public String savePricing(@PathVariable("classCode") int classCode,
            ClassScheduleDTO form,
            @RequestParam(value = "saveMode", defaultValue = "next") String saveMode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        form.setClassCode(classCode);
        form.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            classService.modifyApprovedClassPricing(form);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "가격 정보를 저장했습니다.");
                return editRedirect(classCode, "pricing");
            }
            return editRedirect(classCode, "location");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return editRedirect(classCode, "pricing");
        }
    }

    @GetMapping("/author/classes/{classCode}/edit/location")
    public String locationForm(@PathVariable("classCode") int classCode,
            Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        if (!classService.isApprovedClassEditable(authorCode, classCode)) {
            return classRedirect(classCode);
        }
        model.addAttribute("locationForm", classService.getClassLocation(authorCode, classCode));
        model.addAttribute("kakaoMapAppKey", kakaoMapAppKey);
        addEditModel(model, classCode, "location");
        return "author/class-register-location";
    }

    @PostMapping("/author/classes/{classCode}/edit/location")
    public String saveLocation(@PathVariable("classCode") int classCode,
            ClassLocationDTO form,
            @RequestParam(value = "saveMode", defaultValue = "next") String saveMode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        form.setClassCode(classCode);
        form.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            classService.modifyApprovedClassLocation(form);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "장소 정보를 저장했습니다.");
                return editRedirect(classCode, "location");
            }
            return editRedirect(classCode, "detail");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return editRedirect(classCode, "location");
        }
    }

    @GetMapping("/author/classes/{classCode}/edit/detail")
    public String detailForm(@PathVariable("classCode") int classCode,
            Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        if (!classService.isApprovedClassEditable(authorCode, classCode)) {
            return classRedirect(classCode);
        }
        model.addAttribute("detailForm", classService.getClassDetail(authorCode, classCode));
        addEditModel(model, classCode, "detail");
        return "author/class-register-detail";
    }

    @PostMapping("/author/classes/{classCode}/edit/detail")
    public String saveDetail(@PathVariable("classCode") int classCode,
            ClassDetailDTO form,
            @RequestParam(value = "resultFiles", required = false) List<MultipartFile> resultFiles,
            @RequestParam(value = "saveMode", defaultValue = "next") String saveMode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        form.setClassCode(classCode);
        form.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            classService.modifyApprovedClassDetail(form, resultFiles);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "상세 정보 1/2를 저장했습니다.");
                return editRedirect(classCode, "detail");
            }
            return editRedirect(classCode, "detail-extra");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return editRedirect(classCode, "detail");
        }
    }

    @GetMapping("/author/classes/{classCode}/edit/detail-extra")
    public String detailExtraForm(@PathVariable("classCode") int classCode,
            Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        if (!classService.isApprovedClassEditable(authorCode, classCode)) {
            return classRedirect(classCode);
        }
        model.addAttribute("detailForm", classService.getClassDetail(authorCode, classCode));
        model.addAttribute("offerings", classService.getOfferings());
        addEditModel(model, classCode, "detail-extra");
        return "author/class-register-detail-extra";
    }

    @PostMapping("/author/classes/{classCode}/edit/detail-extra")
    public String saveDetailExtra(@PathVariable("classCode") int classCode,
            ClassDetailDTO form,
            @RequestParam(value = "saveMode", defaultValue = "next") String saveMode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        form.setClassCode(classCode);
        form.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            classService.modifyApprovedClassDetailExtra(form);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "상세 정보 2/2를 저장했습니다.");
                return editRedirect(classCode, "detail-extra");
            }
            return editRedirect(classCode, "curriculum");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return editRedirect(classCode, "detail-extra");
        }
    }

    @GetMapping("/author/classes/{classCode}/edit/curriculum")
    public String curriculumForm(@PathVariable("classCode") int classCode,
            Model model, HttpSession session) {
        long authorCode = AuthorSessionUtils.getAuthorCode(session);
        if (!classService.isApprovedClassEditable(authorCode, classCode)) {
            return classRedirect(classCode);
        }
        model.addAttribute("curriculumForm", classService.getClassCurriculum(authorCode, classCode));
        addEditModel(model, classCode, "curriculum");
        return "author/class-register-curriculum";
    }

    @PostMapping("/author/classes/{classCode}/edit/curriculum")
    public String saveCurriculum(@PathVariable("classCode") int classCode,
            CurriculumFormDTO form,
            @RequestParam(value = "saveMode", defaultValue = "next") String saveMode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        form.setClassCode(classCode);
        form.setAuthorCode(AuthorSessionUtils.getAuthorCode(session));
        try {
            classService.modifyApprovedClassCurriculum(form);
            if (isStay(saveMode)) {
                redirectAttributes.addFlashAttribute("draftSaved", "커리큘럼을 저장했습니다.");
                return editRedirect(classCode, "curriculum");
            }
            redirectAttributes.addFlashAttribute("managementMessage",
                    "클래스 수정사항을 바로 반영했습니다.");
            return classRedirect(classCode);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("registerError", exception.getMessage());
            return editRedirect(classCode, "curriculum");
        }
    }

    private void addEditModel(Model model, int classCode, String editStep) {
        model.addAttribute("editMode", true);
        model.addAttribute("editClassCode", classCode);
        model.addAttribute("editStep", editStep);
        model.addAttribute("editFormAction",
                "/author/classes/" + classCode + "/edit/" + editStep);
    }

    private boolean isStay(String saveMode) {
        return "stay".equalsIgnoreCase(saveMode);
    }

    private String editRedirect(int classCode, String step) {
        return "redirect:/author/classes/" + classCode + "/edit/" + step;
    }

    private String classRedirect(int classCode) {
        return "redirect:/author/classes/" + classCode;
    }
}
