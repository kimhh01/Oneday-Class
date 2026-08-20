package kr.co.oneclass.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileDTO {
    private int memberCode;
    private String name;
    private String phone;
    private String email;
    private String zipCode;
    private String address;
    private String address2;
    private String smsReceiveYN;   
    private String emailReceiveYN;

}