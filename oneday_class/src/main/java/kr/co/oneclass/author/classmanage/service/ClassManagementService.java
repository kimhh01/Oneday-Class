package kr.co.oneclass.author.classmanage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.oneclass.author.classbasic.dao.ClassDAO;
import kr.co.oneclass.author.classbasic.dao.ScheduleDAO;
import kr.co.oneclass.author.classbasic.dto.ClassBasicDTO;
import kr.co.oneclass.author.classbasic.dto.ClassImageDTO;
import kr.co.oneclass.author.classbasic.dto.ClassLocationDTO;
import kr.co.oneclass.author.classbasic.dto.ClassPreviewDTO;
import kr.co.oneclass.author.classbasic.dto.ClassScheduleDTO;
import kr.co.oneclass.author.classbasic.service.ClassService;
import kr.co.oneclass.author.classmanage.dao.ClassManagementDAO;
import kr.co.oneclass.author.classmanage.dto.ClassManagementDTO;
import kr.co.oneclass.author.classmanage.dto.ClassManagementDetailDTO;
import kr.co.oneclass.author.classmanage.dto.ScheduleManageDTO;

@Service
public class ClassManagementService {

    private final ClassManagementDAO cmDAO;
    private final ClassDAO cDAO;
    private final ScheduleDAO sDAO;
    private final ClassService cService;

    public ClassManagementService(ClassManagementDAO cmDAO, ClassDAO cDAO,
            ScheduleDAO sDAO, ClassService cService) {
        this.cmDAO = cmDAO;
        this.cDAO = cDAO;
        this.sDAO = sDAO;
        this.cService = cService;
    }

    // 작가가 운영하는 승인 완료 클래스 목록을 검색·필터링한다
    public List<ClassManagementDTO> getClassManagementList(long authorCode, String classStatus,
            String scheduleType, String keyword) {
        return cmDAO.selectClassManagementList(authorCode, classStatus, scheduleType, keyword);
    }

    // 클래스 등록정보와 일정별 모집 현황을 조합하여 반환한다
    public ClassManagementDetailDTO getClassManagementDetail(long authorCode, int classCode) {
        // TODO: Mapper 연결 후 제거 - classPreview 와 scheduleList 는 classbasic 조회 Mapper
        //       (cDAO.selectClassBasic / selectClassLocation / selectClassSchedule,
        //        ciDAO.selectClassImageListByType, sDAO.selectScheduleManageList) 가 필요하다.
        //       상태·신청인원·예정일정 3개 값은 아래에서 실제 DB 조회로 채운다.
        ClassBasicDTO basic = new ClassBasicDTO();
        basic.setClassCode(classCode);
        basic.setAuthorCode(authorCode);
        basic.setCategoryCode(1);
        basic.setClassTitle("제주 감귤 캔들 만들기");
        basic.setShortIntroduction("제주 감귤 향을 담은 소이 캔들을 직접 만들어요.");
        basic.setClassIntroduction("제주에서 자란 감귤의 향을 담아 나만의 캔들을 완성하는 클래스입니다.");

        ClassLocationDTO location = new ClassLocationDTO();
        location.setClassCode(classCode);
        location.setAddress("제주특별자치도 제주시 한림읍 협재리 1234");
        location.setDetailAddress("숨비당 공방 2층");
        location.setLocationGuide("협재해수욕장 주차장에서 도보 3분");
        location.setLatitude(33.3939);
        location.setLongitude(126.2396);

        ClassScheduleDTO schedule = new ClassScheduleDTO();
        schedule.setClassCode(classCode);
        schedule.setScheduleType("개별일정");
        schedule.setRecruitStartDate(new Date());
        schedule.setRecruitEndDate(new Date());
        schedule.setRegularPrice(65000);
        schedule.setDesiredPrice(45000);
        schedule.setMinPeople(2);
        schedule.setMaxPeople(8);

        ClassImageDTO mainImage = new ClassImageDTO();
        mainImage.setImageCode(1);
        mainImage.setClassCode(classCode);
        mainImage.setImagePath("/author/images/photo/class-flower.png");
        mainImage.setImageType("MAIN");
        mainImage.setImageOrder(1);

        ClassPreviewDTO preview = new ClassPreviewDTO();
        preview.setClassBasic(basic);
        preview.setClassLocation(location);
        preview.setClassSchedule(schedule);
        preview.getMainImageList().add(mainImage);

        ClassManagementDetailDTO detail = new ClassManagementDetailDTO();
        detail.setClassPreview(preview);
        detail.setScheduleList(sampleScheduleList(classCode));

        // 상태와 요약 지표는 실제 DB 값을 쓴다. 다른 작가의 클래스면 조회 결과가 없다
        ClassManagementDTO info = cmDAO.selectClassManagementInfo(authorCode, classCode);
        if (info != null) {
            detail.setClassStatus(info.getClassStatus());
            detail.setTotalApplicantCount(info.getApplicantCount());
            detail.setUpcomingScheduleCount(info.getUpcomingScheduleCount());
            basic.setClassTitle(info.getClassTitle());
            schedule.setDesiredPrice(info.getDesiredPrice());
        }
        return detail;
    }

    // 승인 상태를 확인한 뒤 클래스를 공개한다
    public boolean openClass(long authorCode, int classCode) {
        // TODO: Mapper 연결 후 제거 - cDAO.updateClassStatus(classCode, "OPEN") 으로 교체
        return true;
    }

    // 공개 상태를 확인한 뒤 클래스를 비공개로 변경한다
    public boolean hideClass(long authorCode, int classCode) {
        // TODO: Mapper 연결 후 제거 - cDAO.updateClassStatus(classCode, "PRIVATE") 로 교체
        return true;
    }

    // 현재 신청 인원과 입력값을 검증하고 일정 모집 인원을 변경한다
    public boolean modifySchedulePeople(long authorCode, int scheduleCode, int remainingPeople) {
        // TODO: Mapper 연결 후 제거 - sDAO.updateRemainingPeople(scheduleCode, remainingPeople) 로 교체
        return true;
    }

    // 비밀번호·진행 일정·예약 내역을 검증한 뒤 클래스를 폐쇄한다 (비밀번호는 MEMBER에서 조회)
    public boolean closeClass(long authorCode, int classCode, String password) {
        // TODO: Mapper 연결 후 제거 - countActiveSchedule/countActiveReservation 검증 후 상태 변경
        return true;
    }

    // TODO: Mapper 연결 후 제거 - 화면 확인용 샘플 생성 헬퍼
    private List<ScheduleManageDTO> sampleScheduleList(int classCode) {
        List<ScheduleManageDTO> schedules = new ArrayList<>();
        schedules.add(sampleSchedule(101, classCode, "10:00", "12:00", 6, 8, 2, "RECRUITING"));
        schedules.add(sampleSchedule(102, classCode, "14:00", "16:00", 8, 8, 0, "CLOSED"));
        schedules.add(sampleSchedule(103, classCode, "19:00", "21:00", 3, 8, 5, "RECRUITING"));
        schedules.add(sampleSchedule(104, classCode, "10:00", "12:00", 8, 8, 0, "COMPLETED"));
        return schedules;
    }

    // TODO: Mapper 연결 후 제거 - 화면 확인용 샘플 생성 헬퍼
    private ScheduleManageDTO sampleSchedule(int scheduleCode, int classCode, String startTime,
            String endTime, int reservedCount, int maxPeople, int remainingPeople, String scheduleStatus) {
        ScheduleManageDTO schedule = new ScheduleManageDTO();
        schedule.setScheduleCode(scheduleCode);
        schedule.setClassCode(classCode);
        schedule.setScheduleDate(new Date());
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setMinPeople(2);
        schedule.setMaxPeople(maxPeople);
        schedule.setReservedCount(reservedCount);
        schedule.setRemainingPeople(remainingPeople);
        schedule.setScheduleStatus(scheduleStatus);
        return schedule;
    }
}
