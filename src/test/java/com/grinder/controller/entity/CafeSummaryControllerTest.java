package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.CafeSummaryDTO;
import com.grinder.service.implement.CafeSummaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class CafeSummaryControllerTest {
    @InjectMocks
    CafeSummaryController cafeSummaryController;
    @Mock
    CafeSummaryServiceImpl cafeSummaryService;
    MockMvc mockMvc;
    CafeSummaryDTO.CafeSummaryResponse response;
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(cafeSummaryController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        response = new CafeSummaryDTO.CafeSummaryResponse();
        response.setCafeName("name");
        response.setSummary("summary");
        response.setUpdateTime("2024.07.11");
        response.setCafeAddress("address");
    }
    @Test
    void findCafeSummary() throws Exception {
        when(cafeSummaryService.findCafeSummary(anyString())).thenReturn(response);

        mockMvc.perform(get("/api/cafe_summary/{cafeId}", "testCafeId"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(response)));
    }
}