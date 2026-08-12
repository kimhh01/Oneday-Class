package kr.co.oneclass.payment;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentDAO {
    
    public int insertPayment(PaymentDTO pDTO);
    
    public int updatePaymentStatus(
            @Param("paymentCode") int paymentCode, 
            @Param("status") String status);
    
    public PaymentDTO selectPayment(int paymentCode);
}