package kr.co.oneclass.admin.member;

import java.util.List;

import kr.co.oneclass.admin.common.PageDomain;

public interface AdminMemberService {

	public List<AdminMemberDomain> getMemberList(AdminMemberSearchDTO searchDTO);

	public PageDomain getPage(AdminMemberSearchDTO searchDTO);

	public AdminMemberDomain getMemberDetail(int memberCode);

	public List<AdminMemberReservationDomain> getMemberReservationList(int memberCode, int page);

	public PageDomain getMemberReservationPage(int memberCode, int page);

	public boolean updateMemberStatus(AdminMemberStatusUpdateDTO statusUpdateDTO);

	// 예약 취소 + 결제 환불
	public boolean cancelReservation(int reservationCode);
}