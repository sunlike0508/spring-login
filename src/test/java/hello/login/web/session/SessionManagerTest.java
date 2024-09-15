package hello.login.web.session;

import hello.login.domain.member.Member;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SessionManagerTest {

    private final SessionManager sessionManager = new SessionManager();


    @Test
    void createSession() {

        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

        Member member = new Member();
        member.setName("test");

        sessionManager.createSession(member, httpServletResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(httpServletResponse.getCookies());

        Object result = sessionManager.getSession(request);

        assertThat(result).isEqualTo(member);

        sessionManager.expireSession(request);

        Object result2 = sessionManager.getSession(request);
        assertThat(result2).isNull();
    }
}