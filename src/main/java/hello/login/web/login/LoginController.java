package hello.login.web.login;


import javax.validation.Valid;
import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
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


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }


    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member login = loginService.login(form.getLoginId(), form.getPassword());

        if(login == null) {
            bindingResult.reject("longinFail", "아이도 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        return "redirect:/";
    }
}
