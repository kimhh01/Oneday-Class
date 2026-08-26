package kr.co.oneclass.author.classbasic;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.LocalFileStorageService;

@Service
public class ClassService {

    private static final Logger log = LoggerFactory.getLogger(ClassService.class);
    private static final String MAIN_IMAGE_TYPE = "대표";
    private static final String RESULT_IMAGE_TYPE = "완성";
    private static final String LEGACY_DETAIL_IMAGE_TYPE = "상세";
    private static final String DRAFT_PLACEHOLDER = "작성 중";
    private static final Set<String> EDITABLE_APPROVAL_STATUSES = Set.of("작성중", "수정중");
    private static final Set<String> REGISTER_STEPS = Set.of(
            "basic", "location", "schedule", "detail", "detail-extra", "curriculum", "preview");

    private final ClassDAO cDAO;
    private final ScheduleDAO sDAO;
    private final ClassImageDAO ciDAO;
    private final CurriculumDAO cuDAO;
    private final LocalFileStorageService fileStorageService;

    public ClassService(ClassDAO cDAO, ScheduleDAO sDAO, ClassImageDAO ciDAO,
            CurriculumDAO cuDAO, LocalFileStorageService fileStorageService) {
        this.cDAO = cDAO;
        this.sDAO = sDAO;
        this.ciDAO = ciDAO;
        this.cuDAO = cuDAO;
        this.fileStorageService = fileStorageService;
    }

    // 재작성 클래스와 별개로 일반 신규등록 초안을 하나만 생성한다
    @Transactional
    public int addDraftClass(long authorCode) {
        cDAO.lockDraftOwner(authorCode);
        ClassBasicDTO savedDraft = cDAO.selectLatestDraftClass(authorCode);
        if (savedDraft != null) {
            return savedDraft.getClassCode();
        }

        ClassBasicDTO draft = new ClassBasicDTO();
        draft.setAuthorCode(authorCode);
        if (cDAO.insertDraftClass(draft) != 1) {
            throw new IllegalStateException("클래스 등록을 시작하지 못했습니다.");
        }
        return draft.getClassCode();
    }

    @Transactional
    public int replaceDraftClass(long authorCode, int classCode) {
        cDAO.lockDraftOwner(authorCode);
        ClassBasicDTO draft = cDAO.selectLatestDraftClass(authorCode);
        if (draft == null || draft.getClassCode() != classCode) {
            throw new IllegalArgumentException("삭제할 수 있는 작성 중 클래스가 없습니다.");
        }

        List<String> imagePaths = ciDAO.selectClassImageList(classCode).stream()
                .map(ClassImageDTO::getImagePath)
                .toList();
        List<String> curriculumImagePaths = cuDAO.selectCurriculumStepList(authorCode, classCode).stream()
                .map(CurriculumStepDTO::getImagePath)
                .filter(path -> path != null && !path.isBlank())
                .toList();

        sDAO.deleteScheduleList(classCode);
        sDAO.deleteRepeatScheduleList(classCode);
        cuDAO.deleteCurriculumStepList(classCode);
        ciDAO.deleteClassImageList(classCode);
        cDAO.deleteClassDetailInfoList(classCode);
        cDAO.deleteClassOptionList(classCode);
        cDAO.deleteClassNoticeList(classCode);
        cDAO.deleteClassTagList(classCode);
        cDAO.deleteClassMaterialList(classCode);
        cDAO.deleteClassBookmarkList(classCode);

        if (cDAO.deleteDraftClass(authorCode, classCode) != 1) {
            throw new IllegalArgumentException("삭제할 수 있는 작성 중 클래스가 없습니다.");
        }

        ClassBasicDTO newDraft = new ClassBasicDTO();
        newDraft.setAuthorCode(authorCode);
        if (cDAO.insertDraftClass(newDraft) != 1) {
            throw new IllegalStateException("클래스 등록을 시작하지 못했습니다.");
        }

        List<String> pathsToDelete = new ArrayList<>(imagePaths);
        pathsToDelete.addAll(curriculumImagePaths);
        deleteAfterCommit(pathsToDelete.stream().distinct().toList());
        return newDraft.getClassCode();
    }

    // 가장 최근에 저장한 일반 신규등록 초안을 조회한다
    public ClassBasicDTO getLatestDraftClass(long authorCode) {
        return cDAO.selectLatestDraftClass(authorCode);
    }

    // 마지막으로 열었던 단계를 우선 사용하고, 예전 초안은 필수값으로 재개 단계를 계산한다
    public String getDraftResumePath(long authorCode, int classCode) {
        ClassPreviewDTO preview = getClassPreview(authorCode, classCode);
        if (preview == null) {
            return "/author/classes/register-guide";
        }
        String savedStep = preview.getClassBasic().getRegisterStep();
        if (REGISTER_STEPS.contains(savedStep)) {
            return registerPath(savedStep, classCode);
        }
        if (!isBasicComplete(preview)) {
            return "/author/classes/register/basic?classCode=" + classCode;
        }
        if (!isLocationComplete(preview)) {
            return "/author/classes/register/location?classCode=" + classCode;
        }
        if (!isScheduleComplete(preview)) {
            return "/author/classes/register/schedule?classCode=" + classCode;
        }
        if (!isDetailComplete(preview)) {
            return "/author/classes/register/detail?classCode=" + classCode;
        }
        if (!isCurriculumComplete(preview)) {
            return "/author/classes/register/curriculum?classCode=" + classCode;
        }
        return "/author/classes/register/preview?classCode=" + classCode;
    }

    // 화면을 열 때 현재 단계를 기록해 임시저장 후 정확한 화면에서 이어서 작성한다
    public void markDraftStep(long authorCode, int classCode, String step) {
        if (!REGISTER_STEPS.contains(step)) {
            throw new IllegalArgumentException("알 수 없는 클래스 등록 단계입니다.");
        }
        cDAO.updateRegisterStep(authorCode, classCode, step);
    }

    // 기본정보 단계의 저장된 값을 조회한다
    public ClassBasicDTO getClassBasic(long authorCode, int classCode) {
        ClassBasicDTO basic = cDAO.selectClassBasic(authorCode, classCode);
        if (basic != null) {
            basic.setMainImageList(
                    ciDAO.selectClassImageListByType(classCode, MAIN_IMAGE_TYPE));
        }
        return basic;
    }

    // 승인 상태를 유지하면서 수정할 수 있는 본인 운영 클래스인지 확인한다
    public boolean isApprovedClassEditable(long authorCode, int classCode) {
        return cDAO.existsApprovedEditableClass(authorCode, classCode);
    }

    // 기본정보와 대표사진을 저장한다
    @Transactional
    public boolean modifyClassBasic(ClassBasicDTO cbDTO, List<MultipartFile> mainFiles) {
        return modifyClassBasic(cbDTO, mainFiles, false);
    }

    // 승인 상태를 유지한 채 운영 클래스의 기본정보와 대표사진을 저장한다
    @Transactional
    public boolean modifyApprovedClassBasic(ClassBasicDTO cbDTO, List<MultipartFile> mainFiles) {
        return modifyClassBasic(cbDTO, mainFiles, true);
    }

    private boolean modifyClassBasic(ClassBasicDTO cbDTO, List<MultipartFile> mainFiles,
            boolean approvedEdit) {
        ClassBasicDTO saved = getClassBasic(cbDTO.getAuthorCode(), cbDTO.getClassCode());
        if (saved == null
                || (approvedEdit
                    ? !isApprovedClassEditable(cbDTO.getAuthorCode(), cbDTO.getClassCode())
                    : !isRegistrationEditableStatus(cDAO.selectClassStatus(
                            cbDTO.getAuthorCode(), cbDTO.getClassCode())))) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        mergeDraftBasic(cbDTO, saved);
        validateDraftBasic(cbDTO);

        List<MultipartFile> files = nonEmptyFiles(mainFiles);
        List<ClassImageDTO> oldImages = saved.getMainImageList();
        List<Integer> requestedRemoveCodes = cbDTO.getRemoveMainImageCodeList() == null
                ? List.of() : cbDTO.getRemoveMainImageCodeList();
        Set<Integer> removeCodes = new HashSet<>(requestedRemoveCodes);
        List<ClassImageDTO> removedImages = oldImages.stream()
                .filter(image -> removeCodes.contains(image.getImageCode()))
                .toList();
        if (removedImages.size() != removeCodes.size()) {
            throw new IllegalArgumentException("삭제할 대표 사진 정보를 확인해주세요.");
        }
        Set<Integer> removedCodeSet = removedImages.stream()
                .map(ClassImageDTO::getImageCode)
                .collect(java.util.stream.Collectors.toSet());
        List<ClassImageDTO> remainingImages = oldImages.stream()
                .filter(image -> !removedCodeSet.contains(image.getImageCode()))
                .toList();
        if (remainingImages.size() + files.size() > 5) {
            throw new IllegalArgumentException("대표 사진은 최대 5장까지 등록할 수 있습니다.");
        }
        int nextImageOrder = remainingImages.stream()
                .mapToInt(ClassImageDTO::getImageOrder)
                .max()
                .orElse(0) + 1;
        List<String> storedPaths = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                storedPaths.add(fileStorageService.store(file, "class-main"));
            }

            int updated = approvedEdit
                    ? cDAO.updateApprovedClassBasic(cbDTO)
                    : cDAO.updateClassBasic(cbDTO);
            if (updated != 1) {
                throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
            }

            for (ClassImageDTO removedImage : removedImages) {
                if (ciDAO.deleteClassImage(removedImage.getImageCode()) != 1) {
                    throw new IllegalStateException("기존 대표 사진을 삭제하지 못했습니다.");
                }
            }
            for (int index = 0; index < storedPaths.size(); index++) {
                ClassImageDTO image = new ClassImageDTO();
                image.setClassCode(cbDTO.getClassCode());
                image.setImagePath(storedPaths.get(index));
                image.setImageType(MAIN_IMAGE_TYPE);
                image.setImageOrder(nextImageOrder + index);
                if (ciDAO.insertClassImage(image) != 1) {
                    throw new IllegalStateException("대표 사진 정보를 저장하지 못했습니다.");
                }
            }
            deleteAfterCommit(removedImages.stream().map(ClassImageDTO::getImagePath).toList());
            return true;
        } catch (RuntimeException exception) {
            storedPaths.forEach(this::deleteQuietly);
            throw exception;
        }
    }

    // 위치 단계의 저장된 값을 조회한다
    public ClassLocationDTO getClassLocation(long authorCode, int classCode) {
        return cDAO.selectClassLocation(authorCode, classCode);
    }

    // 주소와 지도 정보를 저장한다
    @Transactional
    public boolean modifyClassLocation(ClassLocationDTO clDTO) {
        return modifyClassLocation(clDTO, false);
    }

    // 승인 상태를 유지한 채 운영 클래스의 장소를 저장한다
    @Transactional
    public boolean modifyApprovedClassLocation(ClassLocationDTO clDTO) {
        return modifyClassLocation(clDTO, true);
    }

    private boolean modifyClassLocation(ClassLocationDTO clDTO, boolean approvedEdit) {
        ClassLocationDTO saved = getClassLocation(clDTO.getAuthorCode(), clDTO.getClassCode());
        if (saved == null
                || (approvedEdit
                    ? !isApprovedClassEditable(clDTO.getAuthorCode(), clDTO.getClassCode())
                    : !isRegistrationEditableStatus(cDAO.selectClassStatus(
                            clDTO.getAuthorCode(), clDTO.getClassCode())))) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        mergeDraftLocation(clDTO, saved);
        validateDraftLocation(clDTO);
        int updated = approvedEdit
                ? cDAO.updateApprovedClassLocation(clDTO)
                : cDAO.updateClassLocation(clDTO);
        if (updated != 1) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        return true;
    }

    // 일정·가격 단계의 저장된 값을 조회한다
    public ClassScheduleDTO getClassSchedule(long authorCode, int classCode) {
        ClassScheduleDTO schedule = cDAO.selectClassSchedule(authorCode, classCode);
        if (schedule == null) {
            return null;
        }
        schedule.setRepeatScheduleList(sDAO.selectRepeatScheduleList(classCode));
        schedule.setScheduleList(sDAO.selectScheduleList(classCode));
        return schedule;
    }

    // 반복 일정 또는 개별 일정과 가격을 저장한다
    @Transactional
    public boolean modifyClassSchedule(ClassScheduleDTO csDTO) {
        ClassScheduleDTO saved = getClassSchedule(csDTO.getAuthorCode(), csDTO.getClassCode());
        if (saved == null) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        mergeDraftSchedule(csDTO, saved);
        validateDraftSchedule(csDTO);

        // 소유자이면서 아직 검수 전인 초안만 수정할 수 있다. 이 검사를 먼저 통과해야
        // 아래의 기존 일정 삭제가 실행되므로 다른 작가·운영 중 클래스는 건드리지 않는다.
        if (cDAO.updateClassSchedule(csDTO) != 1) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }

        sDAO.deleteScheduleList(csDTO.getClassCode());
        sDAO.deleteRepeatScheduleList(csDTO.getClassCode());

        if (csDTO.getScheduleList().isEmpty()) {
            return true;
        }

        Date firstDate = csDTO.getScheduleList().stream()
                .map(ScheduleDTO::getScheduleDate)
                .min(Date::compareTo)
                .orElseThrow();
        Date lastDate = csDTO.getScheduleList().stream()
                .map(ScheduleDTO::getScheduleDate)
                .max(Date::compareTo)
                .orElseThrow();

        RepeatScheduleDTO repeatRule = new RepeatScheduleDTO();
        repeatRule.setClassCode(csDTO.getClassCode());
        repeatRule.setRepeatStartDate(firstDate);
        repeatRule.setRepeatEndDate(lastDate);
        if (sDAO.insertRepeatSchedule(repeatRule) != 1) {
            throw new IllegalStateException("일정 운영 기간을 저장하지 못했습니다.");
        }

        for (ScheduleDTO schedule : csDTO.getScheduleList()) {
            schedule.setClassCode(csDTO.getClassCode());
            schedule.setRepeatRuleCode(repeatRule.getRepeatScheduleCode());
            if (sDAO.insertSchedule(schedule) != 1) {
                throw new IllegalStateException("클래스 일정을 저장하지 못했습니다.");
            }
        }
        return true;
    }

    // 예약이 참조하는 일정은 보존하고 승인 클래스의 가격·재료비 여부만 수정한다
    @Transactional
    public boolean modifyApprovedClassPricing(ClassScheduleDTO csDTO) {
        ClassScheduleDTO saved = getClassSchedule(csDTO.getAuthorCode(), csDTO.getClassCode());
        if (saved == null
                || !isApprovedClassEditable(csDTO.getAuthorCode(), csDTO.getClassCode())) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        mergeDraftSchedule(csDTO, saved);
        validateDraftSchedule(csDTO);
        if (cDAO.updateApprovedClassPricing(csDTO) != 1) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        return true;
    }

    // 상세정보 단계의 저장된 값을 조회한다
    public ClassDetailDTO getClassDetail(long authorCode, int classCode) {
        ClassDetailDTO detail = cDAO.selectClassDetail(authorCode, classCode);
        if (detail == null) {
            return null;
        }
        detail.setResultImageList(selectResultImageList(classCode));
        List<ClassMaterialDTO> materials = cDAO.selectClassMaterialList(classCode);
        detail.setMaterialNameList(materials.stream().map(ClassMaterialDTO::getMaterialName).toList());
        detail.setMaterialContentList(materials.stream().map(ClassMaterialDTO::getMaterialContent).toList());
        return detail;
    }

    // 상세정보 1/2와 완성작 이미지를 저장한다
    @Transactional
    public boolean modifyClassDetail(ClassDetailDTO cdDTO, List<MultipartFile> resultFiles) {
        return modifyClassDetail(cdDTO, resultFiles, false);
    }

    // 승인 상태를 유지한 채 운영 클래스 상세정보 1/2와 완성작 이미지를 저장한다
    @Transactional
    public boolean modifyApprovedClassDetail(ClassDetailDTO cdDTO,
            List<MultipartFile> resultFiles) {
        return modifyClassDetail(cdDTO, resultFiles, true);
    }

    private boolean modifyClassDetail(ClassDetailDTO cdDTO, List<MultipartFile> resultFiles,
            boolean approvedEdit) {
        ClassDetailDTO saved = getClassDetail(cdDTO.getAuthorCode(), cdDTO.getClassCode());
        if (saved == null
                || (approvedEdit
                    ? !isApprovedClassEditable(cdDTO.getAuthorCode(), cdDTO.getClassCode())
                    : !isRegistrationEditableStatus(cDAO.selectClassStatus(
                            cdDTO.getAuthorCode(), cdDTO.getClassCode())))) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        if (trimToNull(cdDTO.getResultDescription()) == null) {
            cdDTO.setResultDescription(saved.getResultDescription());
        }
        validateDraftDetailCore(cdDTO);

        List<MultipartFile> newResultFiles = nonEmptyFiles(resultFiles);
        List<ClassImageDTO> oldResultImages = saved.getResultImageList();
        Set<Integer> removeCodes = new HashSet<>(cdDTO.getRemoveImageCodeList());
        List<ClassImageDTO> removedImages = oldResultImages.stream()
                .filter(image -> removeCodes.contains(image.getImageCode()))
                .toList();
        if (removedImages.size() != removeCodes.size()) {
            throw new IllegalArgumentException("삭제할 이미지 정보를 확인해주세요.");
        }

        Set<Integer> removedCodeSet = removedImages.stream()
                .map(ClassImageDTO::getImageCode)
                .collect(java.util.stream.Collectors.toSet());
        List<ClassImageDTO> remainingResultImages = oldResultImages.stream()
                .filter(image -> !removedCodeSet.contains(image.getImageCode()))
                .toList();
        int remainingResultCount = remainingResultImages.size();
        int nextResultImageOrder = remainingResultImages.stream()
                .mapToInt(ClassImageDTO::getImageOrder)
                .max()
                .orElse(0) + 1;
        if (remainingResultCount + newResultFiles.size() > 4) {
            throw new IllegalArgumentException("완성작 사진은 최대 4장까지 등록할 수 있습니다.");
        }

        List<String> storedPaths = new ArrayList<>();
        try {
            int updated = approvedEdit
                    ? cDAO.updateApprovedClassDetail(cdDTO)
                    : cDAO.updateClassDetail(cdDTO);
            if (updated != 1) {
                throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
            }

            cDAO.deleteClassAdvantageList(cdDTO.getClassCode());
            cDAO.deleteClassRecommendList(cdDTO.getClassCode());

            if (cDAO.insertClassAdvantageList(cdDTO) != cdDTO.getAdvantageList().size()
                    || cDAO.insertClassRecommendList(cdDTO) != cdDTO.getRecommendList().size()) {
                throw new IllegalStateException("상세 정보 항목을 저장하지 못했습니다.");
            }

            for (ClassImageDTO removedImage : removedImages) {
                if (ciDAO.deleteClassImage(removedImage.getImageCode()) != 1) {
                    throw new IllegalStateException("기존 완성작 이미지를 삭제하지 못했습니다.");
                }
            }
            for (MultipartFile file : newResultFiles) {
                String path = fileStorageService.store(file, "class-result");
                storedPaths.add(path);
            }
            for (int index = 0; index < storedPaths.size(); index++) {
                ClassImageDTO image = new ClassImageDTO();
                image.setClassCode(cdDTO.getClassCode());
                image.setImagePath(storedPaths.get(index));
                image.setImageType(RESULT_IMAGE_TYPE);
                image.setImageOrder(nextResultImageOrder + index);
                if (ciDAO.insertClassImage(image) != 1) {
                    throw new IllegalStateException("완성작 이미지 정보를 저장하지 못했습니다.");
                }
            }
            deleteAfterCommit(removedImages.stream().map(ClassImageDTO::getImagePath).toList());
            return true;
        } catch (RuntimeException exception) {
            storedPaths.forEach(this::deleteQuietly);
            throw exception;
        }
    }

    // 상세정보 2/2의 제공사항·준비물·태그를 저장한다
    @Transactional
    public boolean modifyClassDetailExtra(ClassDetailDTO cdDTO) {
        return modifyClassDetailExtra(cdDTO, false);
    }

    // 승인 상태를 유지한 채 운영 클래스 상세정보 2/2를 저장한다
    @Transactional
    public boolean modifyApprovedClassDetailExtra(ClassDetailDTO cdDTO) {
        return modifyClassDetailExtra(cdDTO, true);
    }

    private boolean modifyClassDetailExtra(ClassDetailDTO cdDTO, boolean approvedEdit) {
        ClassDetailDTO saved = getClassDetail(cdDTO.getAuthorCode(), cdDTO.getClassCode());
        if (saved == null
                || (approvedEdit
                    ? !isApprovedClassEditable(cdDTO.getAuthorCode(), cdDTO.getClassCode())
                    : !isRegistrationEditableStatus(cDAO.selectClassStatus(
                            cdDTO.getAuthorCode(), cdDTO.getClassCode())))) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }
        validateDraftDetailExtra(cdDTO);

        try {
            int touched = approvedEdit
                    ? cDAO.guardApprovedClassEdit(cdDTO.getAuthorCode(), cdDTO.getClassCode())
                    : cDAO.touchDraftClass(cdDTO.getAuthorCode(), cdDTO.getClassCode());
            if (touched != 1) {
                throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
            }

            cDAO.deleteClassOptionList(cdDTO.getClassCode());
            cDAO.deleteClassNoticeList(cdDTO.getClassCode());
            cDAO.deleteClassTagList(cdDTO.getClassCode());
            cDAO.deleteClassMaterialList(cdDTO.getClassCode());
            if (cDAO.insertClassOptionList(cdDTO) != cdDTO.getOptionCodeList().size()
                    || cDAO.insertClassNoticeList(cdDTO) != cdDTO.getNoticeList().size()
                    || cDAO.insertClassTagList(cdDTO) != cdDTO.getTagList().size()
                    || cDAO.insertClassMaterialList(cdDTO) != cdDTO.getMaterialNameList().size()) {
                throw new IllegalStateException("추가 정보 항목을 저장하지 못했습니다.");
            }

            return true;
        } catch (DataAccessException exception) {
            log.error("클래스 추가 정보 DB 저장 실패: classCode={}", cdDTO.getClassCode(), exception);
            throw new IllegalStateException("추가 정보를 저장하지 못했습니다. 다시 시도해주세요.");
        }
    }

    // 커리큘럼 단계의 저장된 값을 조회한다
    public CurriculumFormDTO getClassCurriculum(long authorCode, int classCode) {
        CurriculumFormDTO form = new CurriculumFormDTO();
        form.setClassCode(classCode);
        form.setAuthorCode(authorCode);
        List<CurriculumStepDTO> steps = cuDAO.selectCurriculumStepList(authorCode, classCode);
        for (CurriculumStepDTO step : steps) {
            if (DRAFT_PLACEHOLDER.equals(step.getTitle())) {
                step.setTitle("");
            }
            if (DRAFT_PLACEHOLDER.equals(step.getContent())) {
                step.setContent("");
            }
        }
        form.setStepList(steps);
        return form;
    }

    // 커리큘럼 단계 목록과 단계별 이미지를 저장한다
    @Transactional
    public boolean modifyClassCurriculum(CurriculumFormDTO cfDTO) {
        return modifyClassCurriculum(cfDTO, false);
    }

    // 승인 상태를 유지한 채 운영 클래스 커리큘럼을 저장한다
    @Transactional
    public boolean modifyApprovedClassCurriculum(CurriculumFormDTO cfDTO) {
        return modifyClassCurriculum(cfDTO, true);
    }

    private boolean modifyClassCurriculum(CurriculumFormDTO cfDTO, boolean approvedEdit) {
        validateCurriculum(cfDTO);
        if (approvedEdit
                ? !isApprovedClassEditable(cfDTO.getAuthorCode(), cfDTO.getClassCode())
                : !isRegistrationEditableStatus(cDAO.selectClassStatus(
                        cfDTO.getAuthorCode(), cfDTO.getClassCode()))) {
            throw new IllegalArgumentException("수정할 수 없는 클래스입니다.");
        }

        List<CurriculumStepDTO> oldSteps = cuDAO.selectCurriculumStepList(
                cfDTO.getAuthorCode(), cfDTO.getClassCode());
        Map<Integer, CurriculumStepDTO> oldStepMap = new HashMap<>();
        for (CurriculumStepDTO step : oldSteps) {
            oldStepMap.put(step.getCurriculumCode(), step);
        }

        List<String> storedPaths = new ArrayList<>();
        Set<String> retainedPaths = new HashSet<>();
        try {
            for (int index = 0; index < cfDTO.getStepList().size(); index++) {
                CurriculumStepDTO step = cfDTO.getStepList().get(index);
                step.setClassCode(cfDTO.getClassCode());
                step.setStepNo(index + 1);

                MultipartFile imageFile = step.getImageFile();
                if (imageFile != null && !imageFile.isEmpty()) {
                    String path = fileStorageService.store(imageFile, "class-curriculum");
                    storedPaths.add(path);
                    step.setImagePath(path);
                } else {
                    CurriculumStepDTO oldStep = oldStepMap.get(step.getCurriculumCode());
                    if (oldStep != null && trimToNull(oldStep.getImagePath()) != null) {
                        step.setImagePath(oldStep.getImagePath());
                        retainedPaths.add(oldStep.getImagePath());
                    } else {
                        step.setImagePath(null);
                    }
                }
            }

            cuDAO.deleteCurriculumStepList(cfDTO.getClassCode());
            for (CurriculumStepDTO step : cfDTO.getStepList()) {
                if (cuDAO.insertCurriculumStep(step) != 1) {
                    throw new IllegalStateException("커리큘럼을 저장하지 못했습니다.");
                }
            }
            int touched = approvedEdit
                    ? cDAO.guardApprovedClassEdit(cfDTO.getAuthorCode(), cfDTO.getClassCode())
                    : cDAO.touchDraftClass(cfDTO.getAuthorCode(), cfDTO.getClassCode());
            if (touched != 1) {
                throw new IllegalStateException("커리큘럼 저장 시각을 갱신하지 못했습니다.");
            }

            List<String> obsoletePaths = oldSteps.stream()
                    .map(CurriculumStepDTO::getImagePath)
                    .filter(path -> !retainedPaths.contains(path))
                    .toList();
            deleteAfterCommit(obsoletePaths);
            return true;
        } catch (RuntimeException exception) {
            storedPaths.forEach(this::deleteQuietly);
            throw exception;
        }
    }

    // 등록한 모든 단계를 모아 미리보기 정보를 조회한다
    public ClassPreviewDTO getClassPreview(long authorCode, int classCode) {
        ClassBasicDTO basic = getClassBasic(authorCode, classCode);
        if (basic == null) {
            return null;
        }
        ClassPreviewDTO preview = new ClassPreviewDTO();
        preview.setClassBasic(basic);
        preview.setClassLocation(getClassLocation(authorCode, classCode));
        preview.setClassSchedule(getClassSchedule(authorCode, classCode));
        ClassDetailDTO detail = getClassDetail(authorCode, classCode);
        preview.setClassDetail(detail);
        preview.setCurriculum(getClassCurriculum(authorCode, classCode));
        preview.setMainImageList(ciDAO.selectClassImageListByType(classCode, MAIN_IMAGE_TYPE));
        if (detail != null) {
            preview.setResultImageList(detail.getResultImageList());
        }
        return preview;
    }

    // 기존 '상세' 이미지는 완성작으로 호환 조회하고, 새 저장은 '완성작' 타입만 사용한다
    private List<ClassImageDTO> selectResultImageList(int classCode) {
        List<ClassImageDTO> resultImages = new ArrayList<>(
                ciDAO.selectClassImageListByType(classCode, RESULT_IMAGE_TYPE));
        resultImages.addAll(ciDAO.selectClassImageListByType(classCode, LEGACY_DETAIL_IMAGE_TYPE));
        return resultImages;
    }

    // 입력 완성도는 관리자 검수에서 판단하고, 약관·소유자·작성 상태만 확인해 검수를 요청한다
    @Transactional
    public boolean submitClass(ClassSubmitDTO csDTO) {
        if (!csDTO.isServiceTermsAgreed() || !csDTO.isOperationPrivacyAgreed()) {
            throw new IllegalArgumentException("필수 약관에 모두 동의해주세요.");
        }
        if (!isRegistrationEditableStatus(
                cDAO.selectClassStatus(csDTO.getAuthorCode(), csDTO.getClassCode()))) {
            throw new IllegalArgumentException("이미 제출했거나 제출할 수 없는 클래스입니다.");
        }

        if (cDAO.updateClassStatus(csDTO.getAuthorCode(), csDTO.getClassCode(), "대기") != 1) {
            throw new IllegalStateException("클래스 등록 신청을 완료하지 못했습니다.");
        }
        return true;
    }

    // 등록 완료 화면에 출력할 결과 정보를 조회한다
    public ClassRegisterResultDTO getRegisterResult(long authorCode, int classCode) {
        return cDAO.selectRegisterResult(authorCode, classCode);
    }

    // 클래스 카테고리 목록을 조회한다
    public List<CategoryDTO> getCategories() {
        return cDAO.selectCategoryList();
    }

    // 클래스 제공 항목 목록을 조회한다
    public List<OfferingDTO> getOfferings() {
        return cDAO.selectOfferingList();
    }

    private boolean isRegistrationEditableStatus(String approvalStatus) {
        return EDITABLE_APPROVAL_STATUSES.contains(approvalStatus);
    }

    private String registerPath(String step, int classCode) {
        return "/author/classes/register/" + step + "?classCode=" + classCode;
    }

    private void mergeDraftBasic(ClassBasicDTO target, ClassBasicDTO saved) {
        if (target.getCategoryCode() <= 0) {
            target.setCategoryCode(saved.getCategoryCode());
        }
        if (trimToNull(target.getClassTitle()) == null) {
            target.setClassTitle(saved.getClassTitle());
        }
        if (trimToNull(target.getShortIntroduction()) == null) {
            target.setShortIntroduction(saved.getShortIntroduction());
        }
        if (trimToNull(target.getClassIntroduction()) == null) {
            target.setClassIntroduction(saved.getClassIntroduction());
        }
    }

    private void validateDraftBasic(ClassBasicDTO cbDTO) {
        String title = trimToNull(cbDTO.getClassTitle());
        String shortIntroduction = trimToNull(cbDTO.getShortIntroduction());
        String introduction = trimToNull(cbDTO.getClassIntroduction());
        if (cbDTO.getClassCode() <= 0 || cbDTO.getCategoryCode() <= 0) {
            throw new IllegalArgumentException("카테고리를 선택해주세요.");
        }
        if (title == null || utf8Length(title) > 30) {
            throw new IllegalArgumentException("클래스명은 30바이트 이내로 입력해주세요. (한글만 입력 시 최대 10자)");
        }
        if (shortIntroduction == null || utf8Length(shortIntroduction) > 100) {
            throw new IllegalArgumentException("한 줄 소개는 100바이트 이내로 입력해주세요. (한글만 입력 시 최대 33자)");
        }
        if (introduction == null || introduction.length() > 500) {
            throw new IllegalArgumentException("클래스 소개는 500자 이내로 입력해주세요.");
        }
        cbDTO.setClassTitle(title);
        cbDTO.setShortIntroduction(shortIntroduction);
        cbDTO.setClassIntroduction(introduction);
    }

    private int utf8Length(String value) {
        return value.getBytes(StandardCharsets.UTF_8).length;
    }

    private void mergeDraftLocation(ClassLocationDTO target, ClassLocationDTO saved) {
        if (trimToNull(target.getZipcode()) == null) {
            target.setZipcode(saved.getZipcode());
        }
        if (trimToNull(target.getAddress()) == null) {
            target.setAddress(saved.getAddress());
        }
        if (trimToNull(target.getOldAddress()) == null) {
            target.setOldAddress(saved.getOldAddress());
        }
        if (trimToNull(target.getDetailAddress()) == null) {
            target.setDetailAddress(saved.getDetailAddress());
        }
        if (target.getLatitude() == 0 && target.getLongitude() == 0) {
            target.setLatitude(saved.getLatitude());
            target.setLongitude(saved.getLongitude());
        }
    }

    private void validateDraftLocation(ClassLocationDTO clDTO) {
        String zipcode = trimToNull(clDTO.getZipcode());
        String address = trimToNull(clDTO.getAddress());
        String oldAddress = trimToNull(clDTO.getOldAddress());
        String detailAddress = trimToNull(clDTO.getDetailAddress());
        if (zipcode == null || zipcode.length() > 100) {
            throw new IllegalArgumentException("우편번호는 100자 이내로 입력해주세요.");
        }
        if (address == null || address.length() > 100) {
            throw new IllegalArgumentException("도로명 주소는 100자 이내로 입력해주세요.");
        }
        if (detailAddress == null || detailAddress.length() > 100) {
            throw new IllegalArgumentException("상세 주소는 100자 이내로 입력해주세요.");
        }
        if (clDTO.getLatitude() < -90 || clDTO.getLatitude() > 90
                || clDTO.getLongitude() < -180 || clDTO.getLongitude() > 180
                ) {
            throw new IllegalArgumentException("지도에서 주소 위치를 확인해주세요.");
        }
        clDTO.setZipcode(zipcode);
        clDTO.setAddress(address);
        clDTO.setOldAddress(oldAddress == null ? address : oldAddress);
        clDTO.setDetailAddress(detailAddress);
    }

    private void mergeDraftSchedule(ClassScheduleDTO target, ClassScheduleDTO saved) {
        if (trimToNull(target.getScheduleType()) == null) {
            target.setScheduleType(saved.getScheduleType());
        }
        if (!isPositive(target.getRegularPrice())) {
            target.setRegularPrice(saved.getRegularPrice());
        }
        if (!isPositive(target.getDesiredPrice())) {
            target.setDesiredPrice(saved.getDesiredPrice());
        }
        if (!isPositive(target.getMinPeople())) {
            target.setMinPeople(saved.getMinPeople());
        }
        if (!isPositive(target.getMaxPeople())) {
            target.setMaxPeople(saved.getMaxPeople());
        }
    }

    private void validateDraftSchedule(ClassScheduleDTO csDTO) {
        if (csDTO.getClassCode() <= 0) {
            throw new IllegalArgumentException("클래스 정보를 확인해주세요.");
        }
        if (!"ONCE".equals(csDTO.getScheduleType()) && !"REPEAT".equals(csDTO.getScheduleType())) {
            throw new IllegalArgumentException("일정 등록 방식을 확인해주세요.");
        }
        if (csDTO.getRegularPrice() == null || csDTO.getDesiredPrice() == null
                || csDTO.getRegularPrice() < 0 || csDTO.getDesiredPrice() < 0) {
            throw new IllegalArgumentException("가격은 0원 이상 입력해주세요.");
        }
        if (!isPositive(csDTO.getMinPeople()) || !isPositive(csDTO.getMaxPeople())
                || csDTO.getMaxPeople() < csDTO.getMinPeople()) {
            throw new IllegalArgumentException("최소·최대 인원을 확인해주세요.");
        }

        List<ScheduleDTO> schedules = csDTO.getScheduleList();
        if (schedules == null || schedules.isEmpty()) {
            csDTO.setScheduleList(new ArrayList<>());
            return;
        }
        if (schedules.size() > 100) {
            throw new IllegalArgumentException("일정은 최대 100건까지 등록할 수 있습니다.");
        }

        Set<String> uniqueSchedules = new HashSet<>();
        for (ScheduleDTO schedule : schedules) {
            if (schedule.getScheduleDate() == null) {
                throw new IllegalArgumentException("수업 날짜를 확인해주세요.");
            }
            String startTime = validTime(schedule.getStartTime(), "시작 시간을 확인해주세요.");
            String endTime = validTime(schedule.getEndTime(), "종료 시간을 확인해주세요.");
            if (!LocalTime.parse(endTime).isAfter(LocalTime.parse(startTime))) {
                throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
            }
            if (schedule.getMinPeople() <= 0 || schedule.getMaxPeople() < schedule.getMinPeople()) {
                throw new IllegalArgumentException("일정별 최소·최대 인원을 확인해주세요.");
            }
            String uniqueKey = schedule.getScheduleDate().getTime() + ":" + startTime;
            if (!uniqueSchedules.add(uniqueKey)) {
                throw new IllegalArgumentException("같은 날짜와 시작 시간의 일정이 중복되었습니다.");
            }
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
        }
    }

    private void validateDraftDetailCore(ClassDetailDTO cdDTO) {
        if (cdDTO.getClassCode() <= 0) {
            throw new IllegalArgumentException("클래스 정보를 확인해주세요.");
        }
        cdDTO.setAdvantageList(normalizeList(cdDTO.getAdvantageList(), 5, 42, "장점"));
        cdDTO.setRecommendList(normalizeList(cdDTO.getRecommendList(), 5, 42, "추천 대상"));
        String description = trimToNull(cdDTO.getResultDescription());
        if (description == null || description.length() > 500) {
            throw new IllegalArgumentException("완성작 설명은 500자 이내로 입력해주세요.");
        }
        cdDTO.setResultDescription(description);
        normalizeRemoveImageCodes(cdDTO);
    }

    private void validateDraftDetailExtra(ClassDetailDTO cdDTO) {
        if (cdDTO.getClassCode() <= 0) {
            throw new IllegalArgumentException("클래스 정보를 확인해주세요.");
        }
        cdDTO.setNoticeList(normalizeList(cdDTO.getNoticeList(), 5, 80, "추가 유의사항"));
        cdDTO.setTagList(normalizeList(cdDTO.getTagList(), 8, 18, "태그"));
        normalizeMaterials(cdDTO);
        List<Integer> options = cdDTO.getOptionCodeList() == null
                ? new ArrayList<>()
                : new ArrayList<>(new LinkedHashSet<>(cdDTO.getOptionCodeList()));
        if (options.stream().anyMatch(code -> code == null || code <= 0) || options.size() > 5) {
            throw new IllegalArgumentException("제공 항목을 확인해주세요.");
        }
        cdDTO.setOptionCodeList(options);
        normalizeRemoveImageCodes(cdDTO);
    }

    private void normalizeMaterials(ClassDetailDTO cdDTO) {
        List<String> names = cdDTO.getMaterialNameList() == null
                ? List.of() : cdDTO.getMaterialNameList();
        List<String> contents = cdDTO.getMaterialContentList() == null
                ? List.of() : cdDTO.getMaterialContentList();
        if (names.size() != contents.size() || names.size() > 10) {
            throw new IllegalArgumentException("준비물·재료는 최대 10개까지 등록할 수 있습니다.");
        }

        List<String> normalizedNames = new ArrayList<>();
        List<String> normalizedContents = new ArrayList<>();
        Set<String> uniqueNames = new HashSet<>();
        for (int index = 0; index < names.size(); index++) {
            String name = trimToNull(names.get(index));
            String content = trimToNull(contents.get(index));
            if (name == null && content == null) {
                continue;
            }
            if (name == null || content == null) {
                throw new IllegalArgumentException("준비물 이름과 안내 내용을 모두 입력해주세요.");
            }
            if (name.length() > 30 || content.length() > 80) {
                throw new IllegalArgumentException("준비물 이름은 30자, 안내 내용은 80자 이내로 입력해주세요.");
            }
            if (!uniqueNames.add(name.toLowerCase(java.util.Locale.ROOT))) {
                throw new IllegalArgumentException("같은 준비물 이름을 중복 등록할 수 없습니다.");
            }
            normalizedNames.add(name);
            normalizedContents.add(content);
        }
        cdDTO.setMaterialNameList(normalizedNames);
        cdDTO.setMaterialContentList(normalizedContents);
    }

    private void normalizeRemoveImageCodes(ClassDetailDTO cdDTO) {
        List<Integer> removeCodes = cdDTO.getRemoveImageCodeList() == null
                ? new ArrayList<>()
                : new ArrayList<>(new LinkedHashSet<>(cdDTO.getRemoveImageCodeList()));
        if (removeCodes.stream().anyMatch(code -> code == null || code <= 0)) {
            throw new IllegalArgumentException("삭제할 이미지 정보를 확인해주세요.");
        }
        cdDTO.setRemoveImageCodeList(removeCodes);
    }

    private void validateCurriculum(CurriculumFormDTO cfDTO) {
        if (cfDTO.getClassCode() <= 0 || cfDTO.getStepList() == null
                || cfDTO.getStepList().isEmpty() || cfDTO.getStepList().size() > 10) {
            throw new IllegalArgumentException("커리큘럼은 1개 이상 10개까지 등록해주세요.");
        }

        Set<Integer> existingCodes = new HashSet<>();
        for (CurriculumStepDTO step : cfDTO.getStepList()) {
            String title = trimToNull(step.getTitle());
            String content = trimToNull(step.getContent());
            if (title != null && title.length() > 50) {
                throw new IllegalArgumentException("커리큘럼 제목은 50자 이내로 입력해주세요.");
            }
            if (content != null && content.length() > 300) {
                throw new IllegalArgumentException("커리큘럼 내용은 300자 이내로 입력해주세요.");
            }
            if (step.getCurriculumCode() > 0 && !existingCodes.add(step.getCurriculumCode())) {
                throw new IllegalArgumentException("커리큘럼 정보를 다시 확인해주세요.");
            }
            step.setTitle(title == null ? DRAFT_PLACEHOLDER : title);
            step.setContent(content == null ? DRAFT_PLACEHOLDER : content);
        }
    }

    private boolean isBasicComplete(ClassPreviewDTO preview) {
        return preview != null && preview.getClassBasic() != null
                && trimToNull(preview.getClassBasic().getClassTitle()) != null
                && trimToNull(preview.getClassBasic().getShortIntroduction()) != null
                && trimToNull(preview.getClassBasic().getClassIntroduction()) != null
                && !"새 클래스".equals(preview.getClassBasic().getClassTitle())
                && !DRAFT_PLACEHOLDER.equals(preview.getClassBasic().getShortIntroduction())
                && !DRAFT_PLACEHOLDER.equals(preview.getClassBasic().getClassIntroduction())
                && !preview.getMainImageList().isEmpty();
    }

    private boolean isLocationComplete(ClassPreviewDTO preview) {
        return preview.getClassLocation() != null
                && trimToNull(preview.getClassLocation().getZipcode()) != null
                && trimToNull(preview.getClassLocation().getAddress()) != null
                && trimToNull(preview.getClassLocation().getDetailAddress()) != null
                && !"-".equals(preview.getClassLocation().getAddress())
                && (preview.getClassLocation().getLatitude() != 0
                    || preview.getClassLocation().getLongitude() != 0);
    }

    private boolean isScheduleComplete(ClassPreviewDTO preview) {
        return preview.getClassSchedule() != null
                && isPositive(preview.getClassSchedule().getRegularPrice())
                && isPositive(preview.getClassSchedule().getDesiredPrice())
                && !preview.getClassSchedule().getScheduleList().isEmpty();
    }

    private boolean isDetailComplete(ClassPreviewDTO preview) {
        return preview.getClassDetail() != null
                && preview.getClassDetail().getAdvantageList().size() >= 2
                && preview.getClassDetail().getRecommendList().size() >= 2
                && trimToNull(preview.getClassDetail().getResultDescription()) != null
                && !DRAFT_PLACEHOLDER.equals(preview.getClassDetail().getResultDescription())
                && !preview.getResultImageList().isEmpty();
    }

    private boolean isCurriculumComplete(ClassPreviewDTO preview) {
        return preview.getCurriculum() != null && !preview.getCurriculum().getStepList().isEmpty()
                && preview.getCurriculum().getStepList().stream().allMatch(step ->
                    trimToNull(step.getTitle()) != null
                    && trimToNull(step.getContent()) != null
                    && !DRAFT_PLACEHOLDER.equals(step.getTitle())
                    && !DRAFT_PLACEHOLDER.equals(step.getContent())
                    && trimToNull(step.getImagePath()) != null);
    }

    private List<String> normalizeList(List<String> values, int maxCount, int maxLength, String label) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        if (values != null) {
            for (String value : values) {
                String item = trimToNull(value);
                if (item == null) {
                    continue;
                }
                if (item.length() > maxLength) {
                    throw new IllegalArgumentException(label + "은(는) " + maxLength + "자 이내로 입력해주세요.");
                }
                normalized.add(item);
            }
        }
        if (normalized.size() > maxCount) {
            throw new IllegalArgumentException(label + "은(는) 최대 " + maxCount + "개까지 등록할 수 있습니다.");
        }
        return new ArrayList<>(normalized);
    }

    private String validTime(String value, String message) {
        String time = trimToNull(value);
        if (time == null || !time.matches("(?:[01]\\d|2[0-3]):[0-5]\\d")) {
            throw new IllegalArgumentException(message);
        }
        try {
            LocalTime.parse(time);
            return time;
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(message);
        }
    }

    private List<MultipartFile> nonEmptyFiles(List<MultipartFile> files) {
        if (files == null) {
            return new ArrayList<>();
        }
        return files.stream().filter(file -> file != null && !file.isEmpty()).toList();
    }

    private void deleteAfterCommit(List<String> paths) {
        if (paths.isEmpty() || !TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                paths.forEach(ClassService.this::deleteQuietly);
            }
        });
    }

    private void deleteQuietly(String path) {
        try {
            fileStorageService.delete(path);
        } catch (RuntimeException exception) {
            log.warn("클래스 이미지 파일을 정리하지 못했습니다: {}", path, exception);
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isPositive(Integer value) {
        return value != null && value > 0;
    }
}
