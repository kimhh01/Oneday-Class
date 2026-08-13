package kr.co.oneclass.author.classmanage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.author.classbasic.ScheduleDAO;
import kr.co.oneclass.author.classbasic.ClassPreviewDTO;
import kr.co.oneclass.author.classbasic.ClassService;

@Service
public class ClassManagementService {

    private final ClassManagementDAO cmDAO;
    private final ScheduleDAO sDAO;
    private final ClassService cService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClassManagementService(ClassManagementDAO cmDAO,
            ScheduleDAO sDAO, ClassService cService) {
        this.cmDAO = cmDAO;
        this.sDAO = sDAO;
        this.cService = cService;
    }

    // 작가가 운영하는 승인 완료 클래스 목록을 검색·필터링한다
    public List<ClassManagementDTO> getClassManagementList(long authorCode, String classStatus,
            String keyword, LocalDate fromDate, LocalDate toDate) {
        String normalizedStatus = Set.of("모집중", "준비중", "폐강").contains(classStatus)
                ? classStatus : "all";
        String normalizedKeyword = trimToNull(keyword);
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("검색 시작일은 종료일보다 늦을 수 없습니다.");
        }
        return cmDAO.selectClassManagementList(
                authorCode,
                normalizedStatus,
                normalizedKeyword,
                fromDate == null ? null : fromDate.toString(),
                toDate == null ? null : toDate.toString());
    }

    // 클래스 관리 화면 상단에 표시할 실제 운영 지표를 조회한다
    public ClassManagementSummaryDTO getClassManagementSummary(long authorCode) {
        return cmDAO.selectClassManagementSummary(authorCode);
    }

    // 승인 완료된 내 클래스의 전체 일정을 검색·필터링한다
    public List<ScheduleManageDTO> getAuthorScheduleList(long authorCode, String scheduleStatus,
            String keyword, LocalDate fromDate, LocalDate toDate) {
        String normalizedStatus = Set.of("모집중", "모집 마감", "진행 완료")
                .contains(scheduleStatus) ? scheduleStatus : "all";
        String normalizedKeyword = trimToNull(keyword);
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("검색 시작일은 종료일보다 늦을 수 없습니다.");
        }
        return cmDAO.selectAuthorScheduleList(
                authorCode,
                normalizedStatus,
                normalizedKeyword,
                fromDate == null ? null : fromDate.toString(),
                toDate == null ? null : toDate.toString());
    }

    // 현재 조회 결과를 일정 관리 상단 지표로 계산한다
    public ScheduleManagementSummaryDTO summarizeSchedules(List<ScheduleManageDTO> schedules) {
        ScheduleManagementSummaryDTO summary = new ScheduleManagementSummaryDTO();
        summary.setScheduleCount(schedules.size());
        summary.setRecruitingScheduleCount((int) schedules.stream()
                .filter(schedule -> "모집중".equals(schedule.getScheduleStatus()))
                .count());
        summary.setReservedCount(schedules.stream()
                .mapToInt(ScheduleManageDTO::getReservedCount)
                .sum());
        summary.setRemainingSeatCount(schedules.stream()
                .filter(schedule -> "모집중".equals(schedule.getScheduleStatus()))
                .mapToInt(ScheduleManageDTO::getRemainingPeople)
                .sum());
        return summary;
    }

    // 클래스 등록정보와 일정별 모집 현황을 조합하여 반환한다
    public ClassManagementDetailDTO getClassManagementDetail(long authorCode, int classCode) {
        ClassManagementDTO info = cmDAO.selectClassManagementInfo(authorCode, classCode);
        if (info == null) {
            return null;
        }

        ClassPreviewDTO preview = cService.getClassPreview(authorCode, classCode);
        if (preview == null) {
            return null;
        }

        ClassManagementDetailDTO detail = new ClassManagementDetailDTO();
        detail.setClassPreview(preview);
        detail.setScheduleList(sDAO.selectScheduleManageList(classCode));
        detail.setClassStatus(info.getClassStatus());
        detail.setTotalApplicantCount(info.getApplicantCount());
        detail.setUpcomingScheduleCount(info.getUpcomingScheduleCount());
        detail.setSalesAmount(cmDAO.selectClassSalesAmount(authorCode, classCode));
        return detail;
    }

    // 승인된 비공개 클래스(준비중)를 공개 상태(모집중)로 변경한다
    public boolean openClass(long authorCode, int classCode) {
        return cmDAO.updateClassOperationalStatus(
                authorCode, classCode, "준비중", "모집중") == 1;
    }

    // 승인된 공개 클래스(모집중)를 비공개 상태(준비중)로 변경한다
    public boolean hideClass(long authorCode, int classCode) {
        return cmDAO.updateClassOperationalStatus(
                authorCode, classCode, "모집중", "준비중") == 1;
    }

    // 현재 신청 인원과 입력값을 검증하고 일정 모집 인원을 변경한다
    @Transactional
    public boolean modifySchedulePeople(long authorCode, int scheduleCode, int remainingPeople) {
        ScheduleManageDTO schedule = sDAO.selectScheduleManage(scheduleCode);
        ClassManagementDTO classInfo = schedule == null ? null
                : cmDAO.selectClassManagementInfo(authorCode, schedule.getClassCode());
        if (schedule == null || classInfo == null) {
            throw new IllegalArgumentException("수정할 수 없는 클래스 일정입니다.");
        }
        if ("폐강".equals(classInfo.getClassStatus())) {
            throw new IllegalArgumentException("폐강한 클래스의 일정은 변경할 수 없습니다.");
        }
        if ("진행 완료".equals(schedule.getScheduleStatus())) {
            throw new IllegalArgumentException("이미 진행이 끝난 일정은 변경할 수 없습니다.");
        }

        int maximumRemainingPeople = Math.max(
                schedule.getMaxPeople() - schedule.getReservedCount(), 0);
        if (remainingPeople < 0 || remainingPeople > maximumRemainingPeople) {
            throw new IllegalArgumentException(
                    "남은 자리는 0석부터 " + maximumRemainingPeople + "석까지 설정할 수 있습니다.");
        }
        if (sDAO.updateRemainingPeople(authorCode, scheduleCode, remainingPeople) != 1) {
            throw new IllegalArgumentException("일정 정보가 변경되었습니다. 새로고침 후 다시 시도해주세요.");
        }
        return true;
    }

    // 일정을 마감하고 해당 일정의 예약·결제 상태를 취소·환불완료로 변경한다
    @Transactional
    public int cancelSchedule(long authorCode, int classCode, int scheduleCode) {
        ScheduleManageDTO schedule = sDAO.selectScheduleManage(scheduleCode);
        ClassManagementDTO classInfo = cmDAO.selectClassManagementInfo(authorCode, classCode);
        if (schedule == null || classInfo == null || schedule.getClassCode() != classCode) {
            throw new IllegalArgumentException("취소할 수 없는 클래스 일정입니다.");
        }
        if ("폐강".equals(classInfo.getClassStatus())) {
            throw new IllegalArgumentException("이미 폐강한 클래스입니다.");
        }
        if ("진행 완료".equals(schedule.getScheduleStatus())) {
            throw new IllegalArgumentException("이미 진행이 끝난 일정은 취소할 수 없습니다.");
        }
        if (schedule.getRemainingPeople() == 0 && schedule.getReservedCount() == 0) {
            throw new IllegalArgumentException("이미 모집이 마감된 일정입니다.");
        }
        if (cmDAO.countCalculatedSchedulePayment(authorCode, scheduleCode) > 0) {
            throw new IllegalArgumentException("이미 정산에 포함된 결제가 있어 일정을 취소할 수 없습니다.");
        }

        int refundablePaymentCount = cmDAO.countRefundableSchedulePayment(authorCode, scheduleCode);

        if (sDAO.closeSchedule(authorCode, scheduleCode) != 1) {
            throw new IllegalArgumentException("일정 정보가 변경되었습니다. 새로고침 후 다시 시도해주세요.");
        }
        if (cmDAO.refundSchedulePayments(authorCode, scheduleCode) != refundablePaymentCount) {
            throw new IllegalArgumentException("결제 환불 상태를 반영하지 못했습니다. 다시 시도해주세요.");
        }
        return cmDAO.cancelScheduleReservations(authorCode, scheduleCode);
    }

    // 비밀번호·진행 일정·예약 내역을 검증한 뒤 클래스를 폐강한다
    @Transactional
    public boolean closeClass(long authorCode, int classCode, String password) {
        ClassManagementDTO classInfo = cmDAO.selectClassManagementInfo(authorCode, classCode);
        if (classInfo == null) {
            throw new IllegalArgumentException("폐강할 수 없는 클래스입니다.");
        }
        if ("폐강".equals(classInfo.getClassStatus())) {
            throw new IllegalArgumentException("이미 폐강한 클래스입니다.");
        }
        if (!matchesPassword(password, cmDAO.selectAuthorPassword(authorCode))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (cmDAO.countRecruitingSchedule(classCode) > 0) {
            throw new IllegalArgumentException("모집 중인 일정을 모두 취소한 뒤 폐강할 수 있습니다.");
        }
        if (cmDAO.countActiveReservation(classCode) > 0) {
            throw new IllegalArgumentException("취소되지 않은 예약이 있어 폐강할 수 없습니다.");
        }
        if (cmDAO.updateClassClosed(authorCode, classCode) != 1) {
            throw new IllegalArgumentException("클래스 상태가 변경되었습니다. 새로고침 후 다시 시도해주세요.");
        }
        return true;
    }

    private boolean matchesPassword(String rawPassword, String savedPassword) {
        if (rawPassword == null || rawPassword.isBlank() || savedPassword == null) {
            return false;
        }
        if (savedPassword.startsWith("$2a$")
                || savedPassword.startsWith("$2b$")
                || savedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, savedPassword);
        }
        return savedPassword.equals(rawPassword);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
