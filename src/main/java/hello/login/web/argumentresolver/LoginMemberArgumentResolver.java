package hello.login.web.argumentresolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        log.info("supportsParameter 실행");

        boolean longin = methodParameter.hasParameterAnnotation(Login.class);

        boolean assignableFrom = Member.class.isAssignableFrom(methodParameter.getParameterType());

        return longin && assignableFrom;
    }


    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        log.info("resolverArgument 실행");

        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();

        HttpSession session = request.getSession();

        if(session == null) {
            return null;
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
