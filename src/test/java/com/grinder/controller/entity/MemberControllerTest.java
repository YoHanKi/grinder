package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {
    @InjectMocks
    MemberController memberController;
    @Mock
    MemberService memberService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    void addMember() throws Exception {
        MemberDTO.MemberRequestDto requestDto = new MemberDTO.MemberRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setNickname("testuser");
        requestDto.setPassword("password");

        when(memberService.addMember(any(MemberDTO.MemberRequestDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Add Success", "추가되었습니다."))));
    }

    @Test
    void updateMember() throws Exception {
        MemberDTO.MemberUpdateRequestDto requestDto = new MemberDTO.MemberUpdateRequestDto();
        requestDto.setNickname("updateduser");

        when(memberService.updateMember(any(MemberDTO.MemberUpdateRequestDto.class))).thenReturn(true);

        mockMvc.perform(put("/api/member/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Add Success", "변경되었습니다."))));
    }

    @Test
    void checkEmail() throws Exception {
        String email = "test@example.com";

        when(memberService.checkEmail(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/member/email/check")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Email is already in use", "중복된 이메일입니다."))));
    }

    @Test
    void checkNickname() throws Exception {
        String nickname = "testuser";

        when(memberService.checkNickname(anyString())).thenReturn(true);

        mockMvc.perform(get("/api/member/nickname/check")
                        .param("nickname", nickname))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Nickname is already in use", "중복된 닉네임입니다."))));
    }

    @Test
    void sendMessage() throws Exception {
        String email = "test@example.com";

        when(memberService.sendCodeToEmail(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/member/email/verification-requests")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Request Email Verification", "이메일 요청을 완료했습니다."))));
    }

    @Test
    void verificationEmail() throws Exception {
        String email = "test@example.com";
        String authCode = "123456";

        when(memberService.verifiedCode(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(get("/api/member/email/verifications")
                        .param("email", email)
                        .param("code", authCode))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Verify Email Success", "이메일 인증에 성공했습니다."))));
    }

    @Test
    void resetPassword() throws Exception {
        String email = "test@example.com";

        when(memberService.changePassword(anyString())).thenReturn(true);

        mockMvc.perform(patch("/api/member/email/password")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Change Password Success", "비밀번호 변경에 성공했습니다."))));
    }
}