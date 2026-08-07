package kr.co.oneclass.author.profile.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.service.LocalFileStorageService;
import kr.co.oneclass.author.profile.dao.AuthorDAO;
import kr.co.oneclass.author.profile.dto.AuthorProfileDTO;

@Service
public class AuthorService {

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
        return profile == null ? new AuthorProfileDTO() : profile;
    }

    // 프로필 이미지를 저장하고 작가 프로필 정보를 수정한다
    public boolean modifyAuthorProfile(AuthorProfileDTO apDTO, MultipartFile profileFile) {
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
}
