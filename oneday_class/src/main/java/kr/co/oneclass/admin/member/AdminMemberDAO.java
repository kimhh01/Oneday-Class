package kr.co.oneclass.admin.member;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMemberDAO {

	public List<AdminMemberDTO> selectMemberList(@Param("searchDTO") AdminMemberSearchDTO searchDTO,
			@Param("startRow") int startRow, @Param("endRow") int endRow);

	public int selectMemberCount(@Param("searchDTO") AdminMemberSearchDTO searchDTO);

	public AdminMemberDTO selectMemberByCode(@Param("memberCode") int memberCode);

	public List<AdminMemberReservationDTO> selectMemberReservationList(@Param("memberCode") int memberCode,
			@Param("startRow") int startRow, @Param("endRow") int endRow);

	public int selectMemberReservationCount(@Param("memberCode") int memberCode);

	public int updateMemberStatus(AdminMemberStatusUpdateDTO statusUpdateDTO);

	AdminMemberReservationDTO selectReservationForCancel(@Param("reservationCode") int reservationCode);

	int updatePaymentRefund(@Param("reservationCode") int reservationCode);

	int updateReservationCancel(@Param("reservationCode") int reservationCode);
}