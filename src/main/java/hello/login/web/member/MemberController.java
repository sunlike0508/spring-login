package hello.login.web.member;


import javax.validation.Valid;
import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberRepository memberRepository;


    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "member/addMemberForm";
    }


    @PostMapping("/add")
    public String save(@Valid @ModelAttribute Member member, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "member/addMemberForm";
        }

        memberRepository.save(member);

        return "redirect:/";
    }
}
