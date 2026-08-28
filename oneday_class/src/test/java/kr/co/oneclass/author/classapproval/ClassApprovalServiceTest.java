package kr.co.oneclass.author.classapproval;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.oneclass.author.classbasic.ClassDAO;

@ExtendWith(MockitoExtension.class)
class ClassApprovalServiceTest {

    @Mock
    private ClassApprovalDAO classApprovalDAO;
    @Mock
    private ClassDAO classDAO;

    private ClassApprovalService service;

    @BeforeEach
    void setUp() {
        service = new ClassApprovalService(classApprovalDAO, classDAO);
    }

    @Test
    void approvedWaitingClassStartsRecruitingOnlyWhenOneRowChanges() {
        when(classApprovalDAO.startApprovedClass(1L, 2)).thenReturn(1);

        assertTrue(service.startApprovedClass(1L, 2));
        verify(classApprovalDAO).startApprovedClass(1L, 2);
    }

    @Test
    void staleApprovedWaitingClassDoesNotStartRecruiting() {
        when(classApprovalDAO.startApprovedClass(1L, 2)).thenReturn(0);

        assertFalse(service.startApprovedClass(1L, 2));
    }

    @Test
    void suspendedClassReopensOnlyAfterAuthorAction() {
        when(classApprovalDAO.reopenSuspendedClass(1L, 2)).thenReturn(1);

        assertTrue(service.reopenSuspendedClass(1L, 2));
        verify(classApprovalDAO).reopenSuspendedClass(1L, 2);
    }

    @Test
    void rejectedClassReopensThroughDraftTransition() {
        when(classDAO.reopenRejectedClass(1L, 2)).thenReturn(1);

        assertTrue(service.reopenRejectedClass(1L, 2));
        verify(classDAO).reopenRejectedClass(1L, 2);
    }
}
