package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.CafeRegisterDTO.CafeRegisterRequestDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.CafeRegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CafeRegisterControllerTest {
    @InjectMocks
    CafeRegisterController cafeRegisterController;
    @Mock
    CafeRegisterService cafeRegisterService;
    @Mock
    Authentication authentication;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(cafeRegisterController)
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void addCafeRegister_withAuthentication() throws Exception {
        CafeRegisterRequestDTO requestDTO = new CafeRegisterRequestDTO();
        // requestDTO에 필요한 값 설정

        when(cafeRegisterService.saveCafeRegister(anyString(), any(CafeRegisterRequestDTO.class)))
                .thenReturn("registerId");

        SuccessResult expectedResponse = new SuccessResult("user@example.com", "registerId");

        mockMvc.perform(post("/api/cafe_register/newcafe")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(requestDTO))
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));
    }
}