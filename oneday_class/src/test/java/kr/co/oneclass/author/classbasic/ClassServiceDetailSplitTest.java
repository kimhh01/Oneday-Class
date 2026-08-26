package kr.co.oneclass.author.classbasic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.LocalFileStorageService;

@ExtendWith(MockitoExtension.class)
class ClassServiceDetailSplitTest {

    @Mock
    private ClassDAO classDAO;
    @Mock
    private ScheduleDAO scheduleDAO;
    @Mock
    private ClassImageDAO classImageDAO;
    @Mock
    private CurriculumDAO curriculumDAO;
    @Mock
    private LocalFileStorageService fileStorageService;

    private ClassService classService;

    @BeforeEach
    void setUp() {
        classService = new ClassService(classDAO, scheduleDAO, classImageDAO,
                curriculumDAO, fileStorageService);
    }

    @Test
    void startingRegistrationAgainReturnsExistingDraft() {
        ClassBasicDTO draft = new ClassBasicDTO();
        draft.setClassCode(10);
        when(classDAO.selectLatestDraftClass(7L)).thenReturn(draft);

        int classCode = classService.addDraftClass(7L);

        assertEquals(10, classCode);
        verify(classDAO, never()).insertDraftClass(any(ClassBasicDTO.class));
    }

    @Test
    void startingRegistrationCreatesNewDraftWhenNoOrdinaryDraftExists() {
        when(classDAO.insertDraftClass(any(ClassBasicDTO.class))).thenAnswer(invocation -> {
            ClassBasicDTO newDraft = invocation.getArgument(0);
            newDraft.setClassCode(30);
            return 1;
        });

        int classCode = classService.addDraftClass(7L);

        assertEquals(30, classCode);
        verify(classDAO).insertDraftClass(any(ClassBasicDTO.class));
    }

    @Test
    void replacingDraftCannotDeleteClassOutsideOrdinaryDraftList() {
        assertThrows(IllegalArgumentException.class,
                () -> classService.replaceDraftClass(7L, 10));

        verify(scheduleDAO, never()).deleteScheduleList(anyInt());
        verify(classDAO, never()).deleteDraftClass(7L, 10);
    }

    @Test
    void replacingDraftDeletesExistingDataAndCreatesNewDraft() {
        ClassBasicDTO draft = new ClassBasicDTO();
        draft.setClassCode(10);
        when(classDAO.selectLatestDraftClass(7L)).thenReturn(draft);
        when(classImageDAO.selectClassImageList(10)).thenReturn(List.of());
        when(curriculumDAO.selectCurriculumStepList(7L, 10)).thenReturn(List.of());
        when(classDAO.deleteDraftClass(7L, 10)).thenReturn(1);
        when(classDAO.insertDraftClass(any(ClassBasicDTO.class))).thenAnswer(invocation -> {
            ClassBasicDTO newDraft = invocation.getArgument(0);
            newDraft.setClassCode(20);
            return 1;
        });

        int classCode = classService.replaceDraftClass(7L, 10);

        assertEquals(20, classCode);
        verify(scheduleDAO).deleteScheduleList(10);
        verify(scheduleDAO).deleteRepeatScheduleList(10);
        verify(curriculumDAO).deleteCurriculumStepList(10);
        verify(classImageDAO).deleteClassImageList(10);
        verify(classDAO).deleteClassDetailInfoList(10);
        verify(classDAO).deleteClassOptionList(10);
        verify(classDAO).deleteClassNoticeList(10);
        verify(classDAO).deleteClassTagList(10);
        verify(classDAO).deleteClassMaterialList(10);
        verify(classDAO).deleteClassBookmarkList(10);
        verify(classDAO).deleteDraftClass(7L, 10);
    }

    @Test
    void coreFormDoesNotOverwriteExtraInformation() {
        ClassDetailDTO saved = detail(10, 7L);
        when(classDAO.selectClassDetail(7L, 10)).thenReturn(saved);
        when(classDAO.selectClassStatus(7L, 10)).thenReturn("작성중");
        when(classDAO.updateClassDetail(any(ClassDetailDTO.class))).thenReturn(1);
        when(classDAO.insertClassAdvantageList(any(ClassDetailDTO.class))).thenReturn(1);
        when(classDAO.insertClassRecommendList(any(ClassDetailDTO.class))).thenReturn(1);

        ClassDetailDTO form = detail(10, 7L);
        form.setResultDescription("완성작 설명");
        form.setAdvantageList(List.of("쉽게 완성해요"));
        form.setRecommendList(List.of("초보자"));

        classService.modifyClassDetail(form, List.of());

        verify(classDAO, never()).deleteClassOptionList(anyInt());
        verify(classDAO, never()).deleteClassNoticeList(anyInt());
        verify(classDAO, never()).deleteClassTagList(anyInt());
    }

    @Test
    void classTitleOverOracleByteLimitIsRejectedBeforeUpdate() {
        ClassBasicDTO saved = basic(10, 7L);
        when(classDAO.selectClassBasic(7L, 10)).thenReturn(saved);
        when(classDAO.selectClassStatus(7L, 10)).thenReturn("작성중");

        ClassBasicDTO form = basic(10, 7L);
        form.setClassTitle("가나다라마바사아자차카");

        assertThrows(IllegalArgumentException.class,
                () -> classService.modifyClassBasic(form, List.of()));
        verify(classDAO, never()).updateClassBasic(any(ClassBasicDTO.class));
    }

    @Test
    void removingSavedMainImageDeletesOnlySelectedImage() {
        ClassBasicDTO saved = basic(10, 7L);
        ClassImageDTO first = image(1, 1);
        ClassImageDTO second = image(2, 2);
        when(classDAO.selectClassBasic(7L, 10)).thenReturn(saved);
        when(classImageDAO.selectClassImageListByType(10, "대표"))
                .thenReturn(List.of(first, second));
        when(classDAO.selectClassStatus(7L, 10)).thenReturn("작성중");
        when(classDAO.updateClassBasic(any(ClassBasicDTO.class))).thenReturn(1);
        when(classImageDAO.deleteClassImage(2)).thenReturn(1);

        ClassBasicDTO form = basic(10, 7L);
        form.setRemoveMainImageCodeList(List.of(2));

        classService.modifyClassBasic(form, List.of());

        verify(classImageDAO).deleteClassImage(2);
        verify(classImageDAO, never()).deleteClassImage(1);
        verify(classImageDAO, never()).deleteClassImageListByType(anyInt(), anyString());
    }

    @Test
    void extraFormDoesNotOverwriteCoreInformation() {
        ClassDetailDTO saved = detail(10, 7L);
        when(classDAO.selectClassDetail(7L, 10)).thenReturn(saved);
        when(classDAO.selectClassStatus(7L, 10)).thenReturn("작성중");
        when(classDAO.touchDraftClass(7L, 10)).thenReturn(1);

        ClassDetailDTO form = detail(10, 7L);
        form.setTagList(List.of("플라워"));
        when(classDAO.insertClassTagList(any(ClassDetailDTO.class))).thenReturn(1);

        classService.modifyClassDetailExtra(form);

        verify(classDAO, never()).updateClassDetail(any(ClassDetailDTO.class));
        verify(classDAO, never()).deleteClassAdvantageList(anyInt());
        verify(classDAO, never()).deleteClassRecommendList(anyInt());
    }

    @Test
    void extraFormStoresNormalizedMaterialsForSharedClassDetailTable() {
        ClassDetailDTO saved = detail(10, 7L);
        when(classDAO.selectClassDetail(7L, 10)).thenReturn(saved);
        when(classDAO.selectClassStatus(7L, 10)).thenReturn("작성중");
        when(classDAO.touchDraftClass(7L, 10)).thenReturn(1);
        when(classDAO.insertClassMaterialList(any(ClassDetailDTO.class))).thenReturn(1);

        ClassDetailDTO form = detail(10, 7L);
        form.setMaterialNameList(List.of("  앞치마 "));
        form.setMaterialContentList(List.of(" 개별 지참 "));

        classService.modifyClassDetailExtra(form);

        ArgumentCaptor<ClassDetailDTO> captor = ArgumentCaptor.forClass(ClassDetailDTO.class);
        verify(classDAO).insertClassMaterialList(captor.capture());
        assertEquals(List.of("앞치마"), captor.getValue().getMaterialNameList());
        assertEquals(List.of("개별 지참"), captor.getValue().getMaterialContentList());
    }

    @Test
    void emptyScheduleFormDeletesExistingSchedules() {
        ClassScheduleDTO saved = schedule(10, 7L);
        when(classDAO.selectClassSchedule(7L, 10)).thenReturn(saved);
        when(scheduleDAO.selectRepeatScheduleList(10)).thenReturn(List.of());
        when(scheduleDAO.selectScheduleList(10)).thenReturn(List.of());
        when(classDAO.updateClassSchedule(any(ClassScheduleDTO.class))).thenReturn(1);

        ClassScheduleDTO form = schedule(10, 7L);

        classService.modifyClassSchedule(form);

        verify(scheduleDAO).deleteScheduleList(10);
        verify(scheduleDAO).deleteRepeatScheduleList(10);
        verify(scheduleDAO, never()).insertSchedule(any());
    }

    @Test
    void newResultImageUsesNextOrderAfterRemainingMaximum() {
        ClassImageDTO first = image(1, 1);
        ClassImageDTO removed = image(2, 2);
        ClassImageDTO third = image(3, 3);
        when(classDAO.selectClassDetail(7L, 10)).thenReturn(detail(10, 7L));
        when(classImageDAO.selectClassImageListByType(anyInt(), anyString()))
                .thenReturn(List.of(first, removed, third), List.of(), List.of());
        when(classDAO.selectClassStatus(7L, 10)).thenReturn("작성중");
        when(classDAO.updateClassDetail(any(ClassDetailDTO.class))).thenReturn(1);
        when(classImageDAO.deleteClassImage(2)).thenReturn(1);
        when(classImageDAO.insertClassImage(any(ClassImageDTO.class))).thenReturn(1);

        MultipartFile newFile = org.mockito.Mockito.mock(MultipartFile.class);
        when(newFile.isEmpty()).thenReturn(false);
        when(fileStorageService.store(newFile, "class-result"))
                .thenReturn("/upload/author/class-result/new.jpg");

        ClassDetailDTO form = detail(10, 7L);
        form.setResultDescription("완성작 설명");
        form.setRemoveImageCodeList(List.of(2));

        classService.modifyClassDetail(form, List.of(newFile));

        ArgumentCaptor<ClassImageDTO> imageCaptor = ArgumentCaptor.forClass(ClassImageDTO.class);
        verify(classImageDAO).insertClassImage(imageCaptor.capture());
        assertEquals(4, imageCaptor.getValue().getImageOrder());
    }

    @Test
    void revisedClassCanBeSubmittedForReview() {
        ClassSubmitDTO form = new ClassSubmitDTO();
        form.setClassCode(10);
        form.setAuthorCode(7L);
        form.setServiceTermsAgreed(true);
        form.setOperationPrivacyAgreed(true);
        when(classDAO.selectClassStatus(7L, 10)).thenReturn("수정중");
        when(classDAO.updateClassStatus(7L, 10, "대기")).thenReturn(1);

        classService.submitClass(form);

        verify(classDAO).updateClassStatus(7L, 10, "대기");
    }

    private ClassDetailDTO detail(int classCode, long authorCode) {
        ClassDetailDTO detail = new ClassDetailDTO();
        detail.setClassCode(classCode);
        detail.setAuthorCode(authorCode);
        return detail;
    }

    private ClassBasicDTO basic(int classCode, long authorCode) {
        ClassBasicDTO basic = new ClassBasicDTO();
        basic.setClassCode(classCode);
        basic.setAuthorCode(authorCode);
        basic.setCategoryCode(1);
        basic.setClassTitle("도자기 클래스");
        basic.setShortIntroduction("나만의 도자기를 만들어요");
        basic.setClassIntroduction("처음이어도 쉽게 완성하는 클래스입니다.");
        return basic;
    }

    private ClassScheduleDTO schedule(int classCode, long authorCode) {
        ClassScheduleDTO schedule = new ClassScheduleDTO();
        schedule.setClassCode(classCode);
        schedule.setAuthorCode(authorCode);
        schedule.setScheduleType("ONCE");
        schedule.setRegularPrice(50000);
        schedule.setDesiredPrice(45000);
        schedule.setMinPeople(1);
        schedule.setMaxPeople(6);
        return schedule;
    }

    private ClassImageDTO image(int imageCode, int imageOrder) {
        ClassImageDTO image = new ClassImageDTO();
        image.setImageCode(imageCode);
        image.setImageOrder(imageOrder);
        image.setImagePath("/upload/author/class-result/" + imageCode + ".jpg");
        return image;
    }
}
