package kr.co.oneclass.member.domain;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString

public class EmailAuthDomain {

    private String email,authCode,type;
    private Date issueDate;

}
