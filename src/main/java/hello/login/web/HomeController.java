package hello.login.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;


    //@GetMapping("/")
    public String home() {
        return "home";
    }


    @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if(memberId == null) {
            return "home";
        }

        Member member = memberRepository.findById(memberId);

        if(memberId == null) {
            return "home";
        }

        model.addAttribute("member", member);

        return "loginHome";
    }


    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("memberId", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }
}