package kr.co.oneclass.author.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.LocalFileStorageService;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private LocalFileStorageService fileStorageService;
    @Mock
    private AuthorDAO authorDAO;
    @Mock
    private MultipartFile profileFile;

    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        authorService = new AuthorService(fileStorageService, authorDAO);
    }

    @Test
    void successfulProfileImageChangeDeletesPreviousFile() {
        AuthorProfileDTO existing = new AuthorProfileDTO();
        existing.setProfileImagePath("/upload/author/creator/old.jpg");
        AuthorProfileDTO form = new AuthorProfileDTO();
        form.setAuthorCode(7L);
        form.setAuthorNickname("테스트 작가");
        form.setActivityField("서울");
        form.setIntroduction("프로필 소개");
        form.setKakaoUrl("https://pf.kakao.com/_creator");

        when(authorDAO.selectAuthorProfile(7L)).thenReturn(existing);
        when(profileFile.isEmpty()).thenReturn(false);
        when(fileStorageService.store(profileFile, "creator"))
                .thenReturn("/upload/author/creator/new.jpg");
        when(authorDAO.updateAuthorProfile(form)).thenReturn(1);

        authorService.modifyAuthorProfile(form, profileFile);

        verify(fileStorageService).delete("/upload/author/creator/old.jpg");
    }

    @Test
    void invalidSnsUrlIsRejectedBeforeDatabaseUpdate() {
        AuthorProfileDTO form = new AuthorProfileDTO();
        form.setAuthorCode(7L);
        form.setAuthorNickname("테스트 작가");
        form.setActivityField("서울");
        form.setIntroduction("프로필 소개");
        form.setKakaoUrl("javascript:alert(1)");

        assertThrows(IllegalArgumentException.class,
                () -> authorService.modifyAuthorProfile(form, null));
    }

    @Test
    void nonKakaoUrlIsRejectedBeforeDatabaseUpdate() {
        AuthorProfileDTO form = new AuthorProfileDTO();
        form.setAuthorCode(7L);
        form.setAuthorNickname("테스트 작가");
        form.setActivityField("서울");
        form.setIntroduction("프로필 소개");
        form.setKakaoUrl("https://example.com/creator");

        assertThrows(IllegalArgumentException.class,
                () -> authorService.modifyAuthorProfile(form, null));
    }

    @Test
    void kakaoChannelUrlWithoutSchemeIsNormalizedToHttps() {
        AuthorProfileDTO existing = new AuthorProfileDTO();
        AuthorProfileDTO form = new AuthorProfileDTO();
        form.setAuthorCode(7L);
        form.setAuthorNickname("테스트 작가");
        form.setActivityField("서울");
        form.setIntroduction("프로필 소개");
        form.setKakaoUrl("pf.kakao.com/_creator");

        when(authorDAO.selectAuthorProfile(7L)).thenReturn(existing);
        when(authorDAO.updateAuthorProfile(form)).thenReturn(1);

        authorService.modifyAuthorProfile(form, null);

        assertEquals("https://pf.kakao.com/_creator", form.getKakaoUrl());
        verify(authorDAO).updateAuthorProfile(form);
    }
}
