package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.ReportService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {
    @InjectMocks
    private ReportController reportController;
    @Mock
    private ReportService reportService;
    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
        objectMapper = new ObjectMapper();

        // Set up SecurityContext
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void addReport() throws Exception {
        when(reportService.addReport(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/report/{content_id}", "testContentId")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResult("성공", "신고 완료"))))
                .andDo(print());
    }
}