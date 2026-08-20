package kr.co.oneclass.profile;

import kr.co.oneclass.profile.ProfileDAO;
import kr.co.oneclass.member.Member;
import kr.co.oneclass.profile.PassChangeDTO;
import kr.co.oneclass.profile.ProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileDAO profileDAO;

    // Spring Security 암호화가 적용되어 있을 경우 사용 (선택 사항)
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Override
    public Member getProfile(String memberCode) {
        return profileDAO.selectMember(memberCode);
    }

    @Override
    @Transactional
    public boolean updateProfile(ProfileDTO pdto) {
        return profileDAO.updateProfile(pdto) > 0;
    }

    @Override
    @Transactional
    public boolean changeProfileImg(String memberCode, String imgPath) {
        return profileDAO.updateImg(memberCode, imgPath) > 0;
    }

    @Override
    public boolean verifyPassword(String memberCode, String pass) {
        // DAO에서 현재 등록된 비밀번호 조회
        String savedPassword = profileDAO.selectPasswordByMemberId(memberCode, pass);
        
        if (savedPassword == null) {
            return false;
        }

        // 암호화 적용 시 matches 사용, 미적용 시 단순 문자열 비교
        if (passwordEncoder != null) {
            return passwordEncoder.matches(pass, savedPassword);
        }
        return savedPassword.equals(pass);
    }

    @Override
    @Transactional
    public boolean changePassword(PassChangeDTO pdto) {
        // 💡 [필수] 1. 현재 비밀번호 검증 (틀리면 UPDATE를 실행하지 않고 즉시 false 반환)
        boolean isMatched = verifyPassword(String.valueOf(pdto.getMemberCode()), pdto.getCurrentPass());
        if (!isMatched) {
            return false; 
        }

        // 2. 새 비밀번호 암호화 후 DB 저장
        if (passwordEncoder != null) {
            pdto.setNewPass(passwordEncoder.encode(pdto.getNewPass()));
        }
        return profileDAO.updatePass(pdto) > 0;
    }

    @Override
    @Transactional
    public boolean withDraw(String memberCode, String password) {
        // 1. 비밀번호 일치 여부 검증
        boolean isMatched = verifyPassword(memberCode, password);

        // 2. 비밀번호가 일치하면 탈퇴 처리 (상태 변경 또는 삭제)
        if (isMatched) {
            // DAO의 탈퇴 처리 메서드 실행 (예: status를 'WITHDRAW'로 변경)
            return profileDAO.updateStatusToWithdraw(memberCode) > 0;
        }

        return false;
    }
}