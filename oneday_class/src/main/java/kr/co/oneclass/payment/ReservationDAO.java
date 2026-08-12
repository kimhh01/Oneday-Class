package kr.co.oneclass.payment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;

@Mapper
public interface ReservationDAO {
	
	public ReservationDTO selectReservation(int reservationCode);
	
	public int updatePersonCount(
			@Param("reservationCode") int reservationCode,
			@Param("count") int count);
	
	public int updateTotalPrice(
			@Param("reservationCode") int reservationCode,
			@Param("totalPrice") int totalPrice);

	public ClassDTO selectClassDetailByScheduleCode(int scheduleCode);
	public ScheduleDTO selectClassDetailByScheduleCode2(int scheduleCode);
	public CategoryDTO selectCategory(int scheduleCode);
}
