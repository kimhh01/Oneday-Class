package kr.co.oneclass.author.classmanage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.oneclass.author.classbasic.ClassService;
import kr.co.oneclass.author.classbasic.RepeatScheduleDTO;
import kr.co.oneclass.author.classbasic.ScheduleDAO;
import kr.co.oneclass.author.classbasic.ScheduleDTO;

@ExtendWith(MockitoExtension.class)
class ClassManagementServiceTest {

    @Mock
    private ClassManagementDAO classManagementDAO;
    @Mock
    private ScheduleDAO scheduleDAO;
    @Mock
    private ClassService classService;
    @Mock
    private TossPaymentCancellationClient tossCancellationClient;

    private ClassManagementService service;

    @BeforeEach
    void setUp() {
        service = new ClassManagementService(
                classManagementDAO, scheduleDAO, classService, tossCancellationClient);
    }

    @Test
    void tossConfigurationFailureLeavesScheduleAndPaymentUnchanged() {
        long authorCode = 1L;
        int classCode = 2;
        int scheduleCode = 3;

        ScheduleManageDTO schedule = new ScheduleManageDTO();
        schedule.setClassCode(classCode);
        schedule.setScheduleStatus("모집중");
        schedule.setRemainingPeople(4);
        schedule.setReservedCount(1);

        ClassManagementDTO classInfo = new ClassManagementDTO();
        classInfo.setClassStatus("모집중");

        ScheduleRefundPaymentDTO payment = new ScheduleRefundPaymentDTO();
        payment.setPaymentCode(10);
        payment.setPgCode("payment-key");

        when(scheduleDAO.selectScheduleManage(scheduleCode)).thenReturn(schedule);
        when(classManagementDAO.selectClassManagementInfo(authorCode, classCode)).thenReturn(classInfo);
        when(classManagementDAO.countCalculatedSchedulePayment(authorCode, scheduleCode)).thenReturn(0);
        when(classManagementDAO.selectRefundableSchedulePaymentList(authorCode, scheduleCode))
                .thenReturn(List.of(payment));
        org.mockito.Mockito.doThrow(new IllegalArgumentException("dummy key"))
                .when(tossCancellationClient).validateConfiguration();

        assertThrows(IllegalArgumentException.class,
                () -> service.cancelSchedule(authorCode, classCode, scheduleCode));

        verify(scheduleDAO, never()).closeSchedule(authorCode, scheduleCode);
        verify(classManagementDAO, never()).refundSchedulePayments(authorCode, scheduleCode);
        verify(classManagementDAO, never()).cancelScheduleReservations(authorCode, scheduleCode);
    }

    @Test
    void scheduleWithReservationCannotChangeDateTimeOrCapacity() {
        long authorCode = 1L;
        int scheduleCode = 3;
        ScheduleManageDTO saved = new ScheduleManageDTO();
        saved.setClassCode(2);
        saved.setScheduleStatus("모집중");
        saved.setReservedCount(1);

        ClassManagementDTO classInfo = new ClassManagementDTO();
        classInfo.setClassStatus("모집중");
        when(scheduleDAO.selectScheduleManage(scheduleCode)).thenReturn(saved);
        when(classManagementDAO.selectClassManagementInfo(authorCode, 2)).thenReturn(classInfo);

        ScheduleOperationDTO form = validScheduleForm(2);
        assertThrows(IllegalArgumentException.class,
                () -> service.modifySchedule(authorCode, scheduleCode, form));

        verify(scheduleDAO, never()).updateManagedSchedule(authorCode, form);
    }

    @Test
    void newScheduleUsesExistingRepeatRuleAndScheduleSequences() {
        long authorCode = 1L;
        ClassManagementDTO classInfo = new ClassManagementDTO();
        classInfo.setClassStatus("모집중");
        when(classManagementDAO.selectClassManagementInfo(authorCode, 2)).thenReturn(classInfo);
        when(scheduleDAO.countDuplicateSchedule(eq(authorCode), any(ScheduleOperationDTO.class)))
                .thenReturn(0);
        doAnswer(invocation -> {
            RepeatScheduleDTO rule = invocation.getArgument(0);
            rule.setRepeatScheduleCode(11);
            return 1;
        }).when(scheduleDAO).insertRepeatSchedule(any(RepeatScheduleDTO.class));
        doAnswer(invocation -> {
            ScheduleDTO schedule = invocation.getArgument(0);
            schedule.setScheduleCode(12);
            return 1;
        }).when(scheduleDAO).insertSchedule(any(ScheduleDTO.class));

        assertEquals(12, service.addSchedule(authorCode, validScheduleForm(2)));
        verify(scheduleDAO).insertRepeatSchedule(any(RepeatScheduleDTO.class));
        verify(scheduleDAO).insertSchedule(any(ScheduleDTO.class));
    }

    private ScheduleOperationDTO validScheduleForm(int classCode) {
        ScheduleOperationDTO form = new ScheduleOperationDTO();
        form.setClassCode(classCode);
        form.setScheduleDate(LocalDate.now().plusDays(7));
        form.setStartTime("10:00");
        form.setEndTime("12:00");
        form.setMinPeople(1);
        form.setMaxPeople(6);
        return form;
    }
}
