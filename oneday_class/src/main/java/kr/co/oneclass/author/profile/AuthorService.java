package kr.co.oneclass.author.profile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.LocalFileStorageService;
import kr.co.oneclass.common.AESUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthorService {

    private static final String KAKAO_CHANNEL_HOST = "pf.kakao.com";
    private static final Set<String> ACTIVITY_REGIONS = Set.of(
            "서울", "경기", "인천", "부산", "대구", "대전", "광주", "제주");

    private final LocalFileStorageService fileStorageService;
    private final AuthorDAO aDAO;

    public AuthorService(LocalFileStorageService fileStorageService, AuthorDAO aDAO) {
        this.fileStorageService = fileStorageService;
        this.aDAO = aDAO;
    }

    // 작가 프로필 정보를 조회한다
    public AuthorProfileDTO getAuthorProfile(long authorCode) {
        AuthorProfileDTO profile = aDAO.selectAuthorProfile(authorCode);
        // 템플릿이 프로필 값을 바로 참조하므로 작가로 등록되지 않은 코드에도 빈 객체를 돌려준다
        if (profile == null) {
            return new AuthorProfileDTO();
        }
        profile.setAuthorName(AESUtil.decrypt(profile.getAuthorName()));
        profile.setEmail(AESUtil.decrypt(profile.getEmail()));
        return profile;
    }

    // 프로필 이미지를 저장하고 작가 프로필 정보를 수정한다
    @Transactional
    public boolean modifyAuthorProfile(AuthorProfileDTO apDTO, MultipartFile profileFile) {
        validateProfile(apDTO);
        AuthorProfileDTO existing = aDAO.selectAuthorProfile(apDTO.getAuthorCode());
        if (existing == null) {
            return false;
        }

        // 새 이미지를 올렸을 때만 저장하고 경로를 채운다.
        // 비어 있으면 profileImagePath 가 null 이라 Mapper 가 PROFILE_IMAGE 를 건드리지 않는다
        String storedPath = null;
        if (profileFile != null && !profileFile.isEmpty()) {
            storedPath = fileStorageService.store(profileFile, "creator");
            apDTO.setProfileImagePath(storedPath);
        }

        try {
            boolean updated = aDAO.updateAuthorProfile(apDTO) == 1;

            // 대상 작가가 없어 갱신되지 않았으면 방금 올린 파일을 남기지 않는다
            if (!updated && storedPath != null) {
                fileStorageService.delete(storedPath);
            }
            if (updated && storedPath != null && existing.getProfileImagePath() != null
                    && !storedPath.equals(existing.getProfileImagePath())) {
                try {
                    fileStorageService.delete(existing.getProfileImagePath());
                } catch (RuntimeException cleanupException) {
                    log.warn("기존 프로필 이미지 파일을 정리하지 못했습니다: {}",
                            existing.getProfileImagePath(), cleanupException);
                }
            }
            return updated;
        } catch (RuntimeException exception) {
            // SQL 오류가 발생해도 DB에 기록되지 않은 새 파일은 정리한다
            if (storedPath != null) {
                try {
                    fileStorageService.delete(storedPath);
                } catch (RuntimeException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw exception;
        }
    }

    @Transactional
    public boolean registerAuthorProfile(int memberCode, AuthorProfileDTO apDTO,
            MultipartFile profileFile) {
        validateProfile(apDTO);
        apDTO.setMemberCode(memberCode);

        String storedPath = null;
        if (profileFile != null && !profileFile.isEmpty()) {
            storedPath = fileStorageService.store(profileFile, "creator");
            apDTO.setProfileImagePath(storedPath);
        }

        try {
            boolean inserted = aDAO.insertAuthorProfile(apDTO) == 1;
            if (!inserted && storedPath != null) {
                fileStorageService.delete(storedPath);
            }
            return inserted;
        } catch (RuntimeException exception) {
            if (storedPath != null) {
                try {
                    fileStorageService.delete(storedPath);
                } catch (RuntimeException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw exception;
        }
    }

    private void validateProfile(AuthorProfileDTO profile) {
        String nickname = trimToNull(profile.getAuthorNickname());
        if (nickname == null || nickname.length() > 50) {
            throw new IllegalArgumentException("작가명은 50자 이내로 입력해주세요.");
        }
        String region = trimToNull(profile.getActivityField());
        if (!ACTIVITY_REGIONS.contains(region)) {
            throw new IllegalArgumentException("활동 지역을 선택해주세요.");
        }
        String introduction = trimToNull(profile.getIntroduction());
        if (introduction == null || introduction.length() > 300) {
            throw new IllegalArgumentException("소개글은 300자 이내로 입력해주세요.");
        }
        String kakaoUrl = normalizeKakaoChannelUrl(profile.getKakaoUrl());
        profile.setAuthorNickname(nickname);
        profile.setActivityField(region);
        profile.setIntroduction(introduction);
        profile.setKakaoUrl(kakaoUrl);
    }

    private String normalizeKakaoChannelUrl(String value) {
        String candidate = trimToNull(value);
        if (candidate == null) {
            return null;
        }
        if (!candidate.matches("(?i)^https?://.*")) {
            candidate = "https://" + candidate;
        }

        try {
            URI uri = new URI(candidate);
            String path = uri.getRawPath();
            boolean valid = ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    && KAKAO_CHANNEL_HOST.equalsIgnoreCase(uri.getHost())
                    && uri.getUserInfo() == null
                    && uri.getPort() == -1
                    && uri.getRawFragment() == null
                    && path != null
                    && path.startsWith("/_")
                    && path.length() > 2;
            if (!valid) {
                throw invalidKakaoChannelUrl();
            }

            String normalized = "https://" + KAKAO_CHANNEL_HOST + path;
            if (uri.getRawQuery() != null) {
                normalized += "?" + uri.getRawQuery();
            }
            if (normalized.length() > 200) {
                throw invalidKakaoChannelUrl();
            }
            return normalized;
        } catch (URISyntaxException exception) {
            throw invalidKakaoChannelUrl();
        }
    }

    private IllegalArgumentException invalidKakaoChannelUrl() {
        return new IllegalArgumentException(
                "카카오톡 채널 링크는 https://pf.kakao.com/_채널아이디 형식으로 입력해주세요.");
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
