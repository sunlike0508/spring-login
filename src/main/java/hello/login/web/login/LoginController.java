package hello.login.web.login;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }


    //@PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
            HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member login = loginService.login(form.getLoginId(), form.getPassword());

        if(login == null) {
            bindingResult.reject("longinFail", "아이도 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        Cookie cookie = new Cookie("memberId", String.valueOf(login.getId()));

        response.addCookie(cookie);

        return "redirect:/";
    }


    @PostMapping("/login")
    public String login2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
            HttpServletResponse response) {
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member login = loginService.login(form.getLoginId(), form.getPassword());

        if(login == null) {
            bindingResult.reject("longinFail", "아이도 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        sessionManager.createSession(login, response);

        return "redirect:/";
    }


    //@PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("memberId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }


    @PostMapping("/logout")
    public String logout2(HttpServletRequest request) {

        sessionManager.expireSession(request);

        return "redirect:/";
    }
}
