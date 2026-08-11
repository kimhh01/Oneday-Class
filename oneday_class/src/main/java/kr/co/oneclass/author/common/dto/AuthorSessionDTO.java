package kr.co.oneclass.author.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorSessionDTO {

    private long authorCode;
    private int memberCode;
    private String activityName;
    private String approvalStatus;
    private String profileImagePath;
}
