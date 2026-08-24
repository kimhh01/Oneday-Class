package kr.co.oneclass.author.classmanage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.author.classbasic.ScheduleDAO;
import kr.co.oneclass.author.classbasic.ClassPreviewDTO;
import kr.co.oneclass.author.classbasic.RepeatScheduleDTO;
import kr.co.oneclass.author.classbasic.ScheduleDTO;
import kr.co.oneclass.author.classbasic.ClassService;

@Service
public class ClassManagementService {

    private final ClassManagementDAO cmDAO;
    private final ScheduleDAO sDAO;
    private final ClassService cService;
    private final TossPaymentCancellationClient tossCancellationClient;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClassManagementService(ClassManagementDAO cmDAO,
            ScheduleDAO sDAO, ClassService cService,
            TossPaymentCancellationClient tossCancellationClient) {
        this.cmDAO = cmDAO;
        this.sDAO = sDAO;
        this.cService = cService;
        this.tossCancellationClient = tossCancellationClient;
    }

    // 작가가 운영하는 승인 완료 클래스 목록을 검색·필터링한다
    public List<ClassManagementDTO> getClassManagementList(long authorCode, String classStatus,
            String keyword, LocalDate fromDate, LocalDate toDate, int page, int pageSize) {
        String normalizedStatus = normalizeClassStatus(classStatus);
        String normalizedKeyword = trimToNull(keyword);
        validateDateRange(fromDate, toDate);
        int normalizedPage = Math.max(page, 1);
        int normalizedPageSize = Math.max(pageSize, 1);
        return cmDAO.selectClassManagementList(
                authorCode,
                normalizedStatus,
                normalizedKeyword,
                fromDate == null ? null : fromDate.toString(),
                toDate == null ? null : toDate.toString(),
                (normalizedPage - 1) * normalizedPageSize + 1,
                normalizedPage * normalizedPageSize);
    }

    public int getClassManagementCount(long authorCode, String classStatus,
            String keyword, LocalDate fromDate, LocalDate toDate) {
        validateDateRange(fromDate, toDate);
        return cmDAO.selectClassManagementCount(
                authorCode,
                normalizeClassStatus(classStatus),
                trimToNull(keyword),
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

    public List<ClassManagementDTO> getSchedulableClassList(long authorCode) {
        return cmDAO.selectSchedulableClassList(authorCode);
    }

    // 기존 일정 테이블과 시퀀스를 사용해 승인 클래스에 단일 일정을 추가한다
    @Transactional
    public int addSchedule(long authorCode, ScheduleOperationDTO operation) {
        validateScheduleOperation(operation);
        ClassManagementDTO classInfo = cmDAO.selectClassManagementInfo(
                authorCode, operation.getClassCode());
        validateOperationalClass(classInfo);
        operation.setScheduleCode(0);
        if (sDAO.countDuplicateSchedule(authorCode, operation) > 0) {
            throw new IllegalArgumentException("같은 날짜와 시작 시간의 일정이 이미 있습니다.");
        }

        java.sql.Date scheduleDate = java.sql.Date.valueOf(operation.getScheduleDate());
        RepeatScheduleDTO repeatRule = new RepeatScheduleDTO();
        repeatRule.setClassCode(operation.getClassCode());
        repeatRule.setRepeatStartDate(scheduleDate);
        repeatRule.setRepeatEndDate(scheduleDate);
        if (sDAO.insertRepeatSchedule(repeatRule) != 1) {
            throw new IllegalStateException("일정 운영 기간을 저장하지 못했습니다.");
        }

        ScheduleDTO schedule = toScheduleDTO(operation, repeatRule.getRepeatScheduleCode());
        if (sDAO.insertSchedule(schedule) != 1) {
            throw new IllegalStateException("새 일정을 저장하지 못했습니다.");
        }
        return schedule.getScheduleCode();
    }

    // 예약이 없는 미래 일정만 날짜·시간·정원을 변경한다
    @Transactional
    public boolean modifySchedule(long authorCode, int scheduleCode,
            ScheduleOperationDTO operation) {
        operation.setScheduleCode(scheduleCode);
        validateScheduleOperation(operation);
        ScheduleManageDTO saved = sDAO.selectScheduleManage(scheduleCode);
        ClassManagementDTO classInfo = saved == null ? null
                : cmDAO.selectClassManagementInfo(authorCode, saved.getClassCode());
        validateOperationalClass(classInfo);
        if (saved == null || saved.getClassCode() != operation.getClassCode()) {
            throw new IllegalArgumentException("수정할 수 없는 클래스 일정입니다.");
        }
        if ("진행 완료".equals(saved.getScheduleStatus())) {
            throw new IllegalArgumentException("이미 진행이 끝난 일정은 변경할 수 없습니다.");
        }
        if (saved.getReservedCount() > 0) {
            throw new IllegalArgumentException("예약자가 있는 일정은 날짜·시간·정원을 변경할 수 없습니다.");
        }
        if (sDAO.countDuplicateSchedule(authorCode, operation) > 0) {
            throw new IllegalArgumentException("같은 날짜와 시작 시간의 일정이 이미 있습니다.");
        }
        if (sDAO.updateManagedSchedule(authorCode, operation) != 1) {
            throw new IllegalArgumentException("일정 정보가 변경되었습니다. 새로고침 후 다시 시도해주세요.");
        }
        if (sDAO.refreshRepeatRuleRange(authorCode, scheduleCode) != 1) {
            throw new IllegalStateException("일정 운영 기간을 갱신하지 못했습니다.");
        }
        return true;
    }

    // 남은 정원이 있는 미래 마감 일정을 다시 모집 중으로 변경한다
    @Transactional
    public boolean reopenSchedule(long authorCode, int scheduleCode) {
        ScheduleManageDTO schedule = sDAO.selectScheduleManage(scheduleCode);
        ClassManagementDTO classInfo = schedule == null ? null
                : cmDAO.selectClassManagementInfo(authorCode, schedule.getClassCode());
        validateOperationalClass(classInfo);
        if (schedule == null || !"모집 마감".equals(schedule.getScheduleStatus())) {
            throw new IllegalArgumentException("다시 열 수 있는 마감 일정이 아닙니다.");
        }
        if (schedule.getMaxPeople() <= schedule.getReservedCount()) {
            throw new IllegalArgumentException("정원이 모두 예약되어 있어 일정을 다시 열 수 없습니다.");
        }
        if (sDAO.reopenSchedule(authorCode, scheduleCode) != 1) {
            throw new IllegalArgumentException("일정 정보가 변경되었습니다. 새로고침 후 다시 시도해주세요.");
        }
        return true;
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

    // 승인된 비공개 클래스는 바로 공개하지 않고 관리자 공개 승인을 요청한다
    @Transactional
    public boolean requestClassOpenApproval(long authorCode, int classCode) {
        return cmDAO.requestClassOpenApproval(authorCode, classCode) == 1;
    }

    // 승인된 공개 클래스(모집중)를 비공개 상태(준비중)로 변경한다
    @Transactional
    public boolean hideClass(long authorCode, int classCode) {
        return cmDAO.hideClass(authorCode, classCode) == 1;
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

        List<ScheduleRefundPaymentDTO> refundablePayments =
                cmDAO.selectRefundableSchedulePaymentList(authorCode, scheduleCode);
        if (!refundablePayments.isEmpty()) {
            // dummy/미설정 키는 첫 API 호출 전에 차단하여 일정과 DB 결제 상태를 그대로 둔다.
            tossCancellationClient.validateConfiguration();
            for (ScheduleRefundPaymentDTO payment : refundablePayments) {
                tossCancellationClient.cancelFullPayment(
                        payment.getPgCode(), scheduleCode, payment.getPaymentCode());
            }
        }

        if (sDAO.closeSchedule(authorCode, scheduleCode) != 1) {
            throw new IllegalArgumentException("일정 정보가 변경되었습니다. 새로고침 후 다시 시도해주세요.");
        }
        if (cmDAO.refundSchedulePayments(authorCode, scheduleCode) != refundablePayments.size()) {
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

    private String normalizeClassStatus(String classStatus) {
        return Set.of("모집중", "준비중", "폐강").contains(classStatus)
                ? classStatus : "all";
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("검색 시작일은 종료일보다 늦을 수 없습니다.");
        }
    }

    private void validateOperationalClass(ClassManagementDTO classInfo) {
        if (classInfo == null || "폐강".equals(classInfo.getClassStatus())) {
            throw new IllegalArgumentException("일정을 관리할 수 없는 클래스입니다.");
        }
    }

    private void validateScheduleOperation(ScheduleOperationDTO operation) {
        if (operation.getClassCode() <= 0 || operation.getScheduleDate() == null) {
            throw new IllegalArgumentException("클래스와 수업일을 확인해주세요.");
        }
        if (operation.getScheduleDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("지난 날짜에는 일정을 등록할 수 없습니다.");
        }
        LocalTime startTime = parseTime(operation.getStartTime(), "시작");
        LocalTime endTime = parseTime(operation.getEndTime(), "종료");
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 늦어야 합니다.");
        }
        if (operation.getMinPeople() < 1 || operation.getMaxPeople() < operation.getMinPeople()
                || operation.getMaxPeople() > 100) {
            throw new IllegalArgumentException(
                    "최소 인원은 1명 이상, 최대 인원은 최소 인원 이상 100명 이하여야 합니다.");
        }
        operation.setStartTime(startTime.toString());
        operation.setEndTime(endTime.toString());
    }

    private LocalTime parseTime(String value, String label) {
        try {
            return LocalTime.parse(value == null ? "" : value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(label + " 시간을 확인해주세요.");
        }
    }

    private ScheduleDTO toScheduleDTO(ScheduleOperationDTO operation, int repeatRuleCode) {
        ScheduleDTO schedule = new ScheduleDTO();
        schedule.setClassCode(operation.getClassCode());
        schedule.setRepeatRuleCode(repeatRuleCode);
        schedule.setScheduleDate(java.sql.Date.valueOf(operation.getScheduleDate()));
        schedule.setStartTime(operation.getStartTime());
        schedule.setEndTime(operation.getEndTime());
        schedule.setMinPeople(operation.getMinPeople());
        schedule.setMaxPeople(operation.getMaxPeople());
        return schedule;
    }

}
