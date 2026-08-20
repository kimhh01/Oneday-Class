package kr.co.oneclass.payment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.oneclass.common.CategoryDTO;
import kr.co.oneclass.common.ClassDTO;
import kr.co.oneclass.common.ScheduleDTO;

@Mapper
public interface ReservationDAO {
	
	public ReservationDTO selectReservation(int reservationCode);
	
	public ClassDTO selectClassDetailByScheduleCode(int scheduleCode);
	public ScheduleDTO selectClassDetailByScheduleCode2(int scheduleCode);
	public CategoryDTO selectCategory(int scheduleCode);
	
	public int insertReservation(ReservationDTO rDTO);
}
