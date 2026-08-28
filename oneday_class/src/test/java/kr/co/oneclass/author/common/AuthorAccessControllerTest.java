package kr.co.oneclass.author.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import kr.co.oneclass.author.profile.AuthorService;
import kr.co.oneclass.member.Member;

@ExtendWith(MockitoExtension.class)
class AuthorAccessControllerTest {

    @Mock
    private AuthorSessionService authorSessionService;
    @Mock
    private AuthorService authorService;
    private AuthorAccessController controller;
    private MockHttpSession session;
    private Member member;

    @BeforeEach
    void setUp() {
        controller = new AuthorAccessController(authorSessionService, authorService);
        session = new MockHttpSession();
        member = new Member();
        member.setMemberCode(10);
        session.setAttribute("loginMember", member);
    }

    @Test
    void newMemberStartsWithProfileForm() {
        when(authorSessionService.getAuthorByMemberCode(10)).thenReturn(null);

        assertEquals("redirect:/author/access/profile", controller.startAuthor(session));
    }

    @Test
    void incompleteAuthorIsReturnedToProfileForm() {
        AuthorSessionDTO author = new AuthorSessionDTO();
        author.setProfileComplete(false);
        when(authorSessionService.getAuthorByMemberCode(10)).thenReturn(author);

        assertEquals("redirect:/author/access/profile",
                controller.accessGuide(session));
    }

    @Test
    void completeAuthorStartsImmediately() {
        AuthorSessionDTO author = new AuthorSessionDTO();
        author.setProfileComplete(true);
        when(authorSessionService.getAuthorByMemberCode(10)).thenReturn(author);

        assertEquals("redirect:/author", controller.accessGuide(session));
        verify(authorSessionService).activateCompletedAuthor(10);
    }
}
