package hello.login.web.session;


import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    private static final String SESSION_COOKIE_NAME = "mySessionId";
    private final Map<String, Object> sessionMap = new ConcurrentHashMap<>();


    /**
     * 세션 생성
     */
    public void createSession(Object value, HttpServletResponse response) {
        String sessionId = UUID.randomUUID().toString();

        sessionMap.put(sessionId, value);

        response.addCookie(new Cookie(SESSION_COOKIE_NAME, sessionId));
    }


    public Object getSession(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);

        if(cookie == null) {
            return null;
        }

        return sessionMap.get(cookie.getValue());
    }


    public void expireSession(HttpServletRequest request) {
        Cookie cookies = findCookie(request, SESSION_COOKIE_NAME);

        if(cookies != null) {
            sessionMap.remove(cookies.getValue());
        }
    }


    public Cookie findCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if(request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findFirst().orElse(null);
    }
}
