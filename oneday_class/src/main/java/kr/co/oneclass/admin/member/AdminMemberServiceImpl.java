package kr.co.oneclass.admin.member;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.oneclass.admin.common.PageDomain;

@Service
public class AdminMemberServiceImpl implements AdminMemberService {

	private static final int PAGE_SIZE = 10;
	private static final int RESERVATION_PAGE_SIZE = 5;

	private final AdminMemberDAO adminMemberDAO;

	public AdminMemberServiceImpl(AdminMemberDAO adminMemberDAO) {

		this.adminMemberDAO = adminMemberDAO;
	}

	@Override
	public List<AdminMemberDomain> getMemberList(AdminMemberSearchDTO searchDTO) {

		int page = Math.max(searchDTO.getPage(), 1);

		int startRow = (page - 1) * PAGE_SIZE + 1;

		int endRow = page * PAGE_SIZE;

		return adminMemberDAO.selectMemberList(searchDTO, startRow, endRow).stream().map(AdminMemberDTO::toDomain)
				.toList();
	}

	@Override
	public PageDomain getPage(AdminMemberSearchDTO searchDTO) {

		int currentPage = Math.max(searchDTO.getPage(), 1);

		int totalCount = adminMemberDAO.selectMemberCount(searchDTO);

		int totalPage = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / PAGE_SIZE);

		int startPage = ((currentPage - 1) / 5) * 5 + 1;

		int endPage = Math.min(startPage + 4, totalPage);

		return new PageDomain(totalCount, currentPage, PAGE_SIZE, totalPage, startPage, endPage);
	}

	@Override
	public AdminMemberDomain getMemberDetail(int memberCode) {

		AdminMemberDTO dto = adminMemberDAO.selectMemberByCode(memberCode);

		return dto == null ? null : dto.toDomain();
	}

	@Override
	public List<AdminMemberReservationDomain> getMemberReservationList(int memberCode, int page) {

		int currentPage = Math.max(page, 1);

		int startRow = (currentPage - 1) * RESERVATION_PAGE_SIZE + 1;

		int endRow = currentPage * RESERVATION_PAGE_SIZE;

		return adminMemberDAO.selectMemberReservationList(memberCode, startRow, endRow).stream()
				.map(AdminMemberReservationDTO::toDomain).toList();
	}

	@Override
	public PageDomain getMemberReservationPage(int memberCode, int page) {

		int currentPage = Math.max(page, 1);

		int totalCount = adminMemberDAO.selectMemberReservationCount(memberCode);

		int totalPage = totalCount == 0 ? 1 : (int) Math.ceil((double) totalCount / RESERVATION_PAGE_SIZE);

		int startPage = ((currentPage - 1) / 5) * 5 + 1;

		int endPage = Math.min(startPage + 4, totalPage);

		return new PageDomain(totalCount, currentPage, RESERVATION_PAGE_SIZE, totalPage, startPage, endPage);
	}

	@Override
	@Transactional
	public boolean updateMemberStatus(AdminMemberStatusUpdateDTO statusUpdateDTO) {

		return adminMemberDAO.updateMemberStatus(statusUpdateDTO) > 0;
	}

	@Override
	@Transactional
	public boolean cancelReservation(int reservationCode) {

		/*
		 * 1. 현재 DB의 예약 상태를 다시 확인
		 */
		AdminMemberReservationDTO reservation = adminMemberDAO.selectReservationForCancel(reservationCode);

		if (reservation == null) {
			return false;
		}

		/*
		 * 예약 상태가 '예약'인 경우에만 환불 가능
		 */
		if (!"예약".equals(reservation.getReservationStatus())) {

			return false;
		}

		/*
		 * 2. PAYMENT 환불 처리
		 *
		 * REFUND = AMOUNT STATUS = '환불완료' REFUND_DATE = SYSDATE
		 */
		int paymentResult = adminMemberDAO.updatePaymentRefund(reservationCode);

		if (paymentResult != 1) {

			throw new IllegalStateException("결제 환불 처리에 실패했습니다.");
		}

		/*
		 * 3. RESERVATION 취소 처리
		 *
		 * STATUS = '취소'
		 */
		int reservationResult = adminMemberDAO.updateReservationCancel(reservationCode);

		if (reservationResult != 1) {

			throw new IllegalStateException("예약 취소 처리에 실패했습니다.");
		}

		return true;
	}
}