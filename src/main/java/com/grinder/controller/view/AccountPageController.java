package com.grinder.controller.view;

import com.grinder.domain.entity.Member;
import com.grinder.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class AccountPageController {
    private final MemberRepository memberRepository;

    //회원가입 로그인 페이지 통일해야되면 이름 수정하기 -> 나중에!
    @GetMapping("/page/login")
    public String viewLoginPage() {
        return "loginForm";
    }

    @GetMapping("/page/signup")
    public String viewSignupPage(@RequestParam(name = "email", required = false) String email){
        return "addMemberForm";
    }
    // 비밀번호 변경
    @GetMapping("/page/change/password")
    public String viewFindAccountPage(){return "findAccountForm";}

    @GetMapping("/page/welcome")
    public String viewWelcomePage(){return "welcomeForm";}

    @GetMapping("/page/change/password/finish")
    public String viewFindAccountFinishPage(){return "findAccountFinishForm";}

    @GetMapping("/page/change/memberInfo/{member_id}")
    public String viewUpdateMemberInfo(@PathVariable("member_id")String memberId) {
        String email = getEmail();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        if (!member.getEmail().equals(email)) {
            throw new IllegalArgumentException("본인만 수정할 수 있습니다.");
        }
        return "updateMemberForm";
    }

    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication.getName()).orElse(null);
    }
}
