package com.grinder.controller.view;

import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.Role;
import com.grinder.service.MemberService;
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
import org.springframework.ui.Model;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    @InjectMocks
    private IndexController indexController;

    @Mock
    private MemberService memberService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();

        // Setup security context
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("test@test.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void index_AuthenticatedUser() throws Exception {
        // Mock member service to return a member DTO
        MemberDTO.FindMemberDTO memberDTO = new MemberDTO.FindMemberDTO(Member.builder()
                .email("test@test.com")
                .nickname("testUser")
                .role(Role.MEMBER)
                .build());
        when(authentication.getName()).thenReturn("test@test.com");
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(anyString())).thenReturn(member);

        // Perform the request and verify the view name and model attributes
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void index_AnonymousUser() throws Exception {
        // Mock member service to return null for anonymous user
        when(authentication.getName()).thenReturn("anonymousUser");

        // Perform the request and verify the view name and model attributes
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}