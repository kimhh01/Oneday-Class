package kr.co.oneclass.author.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import kr.co.oneclass.author.common.LocalFileStorageService;
import kr.co.oneclass.common.AESUtil;

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
    void profileNameAndEmailAreDecryptedForDisplay() {
        AuthorProfileDTO stored = new AuthorProfileDTO();
        stored.setAuthorName(AESUtil.encrypt("테스트1"));
        stored.setEmail(AESUtil.encrypt("test@naver.com"));
        when(authorDAO.selectAuthorProfile(7L)).thenReturn(stored);

        AuthorProfileDTO profile = authorService.getAuthorProfile(7L);

        assertEquals("테스트1", profile.getAuthorName());
        assertEquals("test@naver.com", profile.getEmail());
    }

    @Test
    void successfulProfileImageChangeDeletesPreviousFile() {
        AuthorProfileDTO existing = new AuthorProfileDTO();
        existing.setProfileImagePath("/upload/creator/old.jpg");
        AuthorProfileDTO form = new AuthorProfileDTO();
        form.setAuthorCode(7L);
        form.setAuthorNickname("테스트 작가");
        form.setActivityField("서울");
        form.setIntroduction("프로필 소개");
        form.setKakaoUrl("https://pf.kakao.com/_creator");

        when(authorDAO.selectAuthorProfile(7L)).thenReturn(existing);
        when(profileFile.isEmpty()).thenReturn(false);
        when(fileStorageService.store(profileFile, "creator"))
                .thenReturn("/upload/creator/new.jpg");
        when(authorDAO.updateAuthorProfile(form)).thenReturn(1);

        authorService.modifyAuthorProfile(form, profileFile);

        verify(fileStorageService).delete("/upload/creator/old.jpg");
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

    @Test
    void authorRegistrationRequiresProfileFields() {
        AuthorProfileDTO form = new AuthorProfileDTO();
        form.setAuthorNickname("신규 작가");
        form.setActivityField("서울");

        assertThrows(IllegalArgumentException.class,
                () -> authorService.registerAuthorProfile(10, form, null));
    }

    @Test
    void completeProfileCreatesAuthor() {
        AuthorProfileDTO form = new AuthorProfileDTO();
        form.setAuthorNickname("신규 작가");
        form.setActivityField("서울");
        form.setIntroduction("새로운 클래스를 준비하고 있습니다.");

        when(authorDAO.insertAuthorProfile(form)).thenReturn(1);

        assertTrue(authorService.registerAuthorProfile(10, form, null));
        assertEquals(10, form.getMemberCode());
        verify(authorDAO).insertAuthorProfile(form);
    }
}
