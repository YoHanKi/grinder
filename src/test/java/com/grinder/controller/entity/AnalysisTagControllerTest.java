package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.entity.AnalysisTag;
import com.grinder.domain.entity.Member;
import com.grinder.service.AnalysisTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnalysisTagControllerTest {
    @InjectMocks
    AnalysisTagController analysisTagController;
    @Mock
    AnalysisTagService analysisTagService;
    @Mock
    Authentication authentication;

    MockMvc mockMvc;

    AnalysisTag analysisTagData;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(analysisTagController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("test@test.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("test@test.com");

        analysisTagData = new AnalysisTag(1L, new Member(), "tag");
    }

    @Test
    void saveAlanTag() throws Exception {
        List<String> tags = List.of("tag1", "tag2", "tag3");

        Mockito.when(analysisTagService.findByEmail(anyString())).thenReturn(analysisTagData);
        Mockito.when(analysisTagService.addTagList(anyList(), any())).thenReturn(true);

        mockMvc.perform(post("/saveAlanTag")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tags)))
                .andExpect(status().isOk());
    }
}