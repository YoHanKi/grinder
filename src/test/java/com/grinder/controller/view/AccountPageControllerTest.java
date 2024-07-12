package com.grinder.controller.view;

import com.grinder.domain.entity.Member;
import com.grinder.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class AccountPageControllerTest {

    @InjectMocks
    private AccountPageController accountPageController;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(accountPageController).build();

        // Set up SecurityContext
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void viewLoginPage() throws Exception {
        mockMvc.perform(get("/page/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("loginForm"));
    }

    @Test
    void viewSignupPage() throws Exception {
        mockMvc.perform(get("/page/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("addMemberForm"));
    }

    @Test
    void viewFindAccountPage() throws Exception {
        mockMvc.perform(get("/page/change/password"))
                .andExpect(status().isOk())
                .andExpect(view().name("findAccountForm"));
    }

    @Test
    void viewWelcomePage() throws Exception {
        mockMvc.perform(get("/page/welcome"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcomeForm"));
    }

    @Test
    void viewFindAccountFinishPage() throws Exception {
        mockMvc.perform(get("/page/change/password/finish"))
                .andExpect(status().isOk())
                .andExpect(view().name("findAccountFinishForm"));
    }

    @Test
    void viewUpdateMemberInfo() throws Exception {
        Member member = Member.builder().email("user@example.com").build();

        when(memberRepository.findById(anyString())).thenReturn(Optional.of(member));

        mockMvc.perform(get("/page/change/memberInfo/{member_id}", "testMemberId"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateMemberForm"));
    }
}