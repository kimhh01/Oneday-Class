package kr.co.oneclass.author.profile.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorProfileDTO {

    private long authorCode;           // 작가 코드
    private String authorName;        // 작가 실명
    private String email;             // 이메일
    private String authorNickname;    // 활동명
    private String introduction;      // 작가 소개
    private String activityField;     // 활동 분야
    private String kakaoUrl;          // 카카오 채널 URL
    private String profileImagePath;  // 프로필 이미지 경로
    private String openStatus;        // 프로필 공개 여부
}
