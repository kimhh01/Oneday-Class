package kr.co.oneclass.profile;

import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.PassChangeDTO;
import kr.co.oneclass.profile.ProfileDTO;

public interface ProfileService {

    /**
     * 회원 프로필 상세 정보 조회
     */
    Member getProfile(String memberCode);

    /**
     * 프로필 정보 수정 (이름, 핸드폰 번호, 이메일)
     */
    boolean updateProfile(ProfileDTO pdto);

    /**
     * 프로필 이미지 경로 변경
     */
    boolean changeProfileImg(String memberCode, String imgPath);

    /**
     * 회원 비밀번호 검증
     */
    boolean verifyPassword(String memberCode, String pass);

    /**
     * 비밀번호 변경
     */
    boolean changePassword(PassChangeDTO pdto);

    /**
     * 회원 탈퇴
     */
    boolean withDraw(String memberCode, String password);
}