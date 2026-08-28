package kr.co.oneclass.profile;

import kr.co.oneclass.common.AESUtil; // 💡 AESUtil 임포트 추가
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

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Override
    public Member getProfile(String memberCode) {
        Member member = profileDAO.selectMember(memberCode);
        
        // 💡 1. DB에서 조회한 프로필 정보(이름, 이메일, 전화번호) 복호화
        if (member != null) {
            if (member.getName() != null) member.setName(AESUtil.decrypt(member.getName()));
            if (member.getEmail() != null) member.setEmail(AESUtil.decrypt(member.getEmail()));
            if (member.getPhone() != null) member.setPhone(AESUtil.decrypt(member.getPhone()));
        }
        
        return member;
    }

    @Override
    @Transactional
    public boolean updateProfile(ProfileDTO pdto) {
        // 💡 2. 프로필 수정 저장 시 개인정보 양방향 암호화 처리 후 DB 저장
        if (pdto != null) {
            if (pdto.getName() != null) pdto.setName(AESUtil.encrypt(pdto.getName()));
            if (pdto.getEmail() != null) pdto.setEmail(AESUtil.encrypt(pdto.getEmail()));
            if (pdto.getPhone() != null) pdto.setPhone(AESUtil.encrypt(pdto.getPhone()));
        }
        
        return profileDAO.updateProfile(pdto) > 0;
    }

    @Override
    @Transactional
    public boolean changeProfileImg(String memberCode, String imgPath) {
        return profileDAO.updateImg(memberCode, imgPath) > 0;
    }

    @Override
    public boolean verifyPassword(String memberCode, String pass) {
        String savedPassword = profileDAO.selectPasswordByMemberId(memberCode, pass);
        
        if (savedPassword == null) {
            return false;
        }

        if (passwordEncoder != null) {
            return passwordEncoder.matches(pass, savedPassword);
        }
        return savedPassword.equals(pass);
    }

    @Override
    @Transactional
    public boolean changePassword(PassChangeDTO pdto) {
        boolean isMatched = verifyPassword(String.valueOf(pdto.getMemberCode()), pdto.getCurrentPass());
        if (!isMatched) {
            return false; 
        }

        if (passwordEncoder != null) {
            pdto.setNewPass(passwordEncoder.encode(pdto.getNewPass()));
        }
        return profileDAO.updatePass(pdto) > 0;
    }

    @Override
    @Transactional
    public boolean withDraw(String memberCode, String password) {
        boolean isMatched = verifyPassword(memberCode, password);

        if (isMatched) {
            return profileDAO.updateStatusToWithdraw(memberCode) > 0;
        }

        return false;
    }
}