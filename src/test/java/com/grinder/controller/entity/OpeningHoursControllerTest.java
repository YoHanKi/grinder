package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.grinder.domain.dto.OpeningHoursDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.OpeningHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OpeningHoursControllerTest {
    @InjectMocks
    OpeningHoursController openingHoursController;
    @Mock
    OpeningHoursService openingHoursService;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(openingHoursController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void saveNewOpeningHours() throws Exception {
        List<OpeningHoursDTO.saveOpeningRequest> requestList = List.of(
                new OpeningHoursDTO.saveOpeningRequest("Monday", "09:00", "18:00", false),
                new OpeningHoursDTO.saveOpeningRequest("Tuesday", "09:00", "18:00", false)
        );

        when(openingHoursService.saveOpeningHours(anyString(), anyList())).thenReturn(true);

        mockMvc.perform(post("/api/cafe_register/{register_id}/opening_hours", "testRegisterId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResult("성공", "등록이 성공하였습니다."))));
    }

    @Test
    void saveOpeningHours() throws Exception {
        List<OpeningHoursDTO.saveOpeningRequest> requestList = List.of(
                new OpeningHoursDTO.saveOpeningRequest("Monday", "09:00", "18:00", false),
                new OpeningHoursDTO.saveOpeningRequest("Tuesday", "09:00", "18:00", false)
        );

        when(openingHoursService.saveOpeningHours(anyString(), anyList())).thenReturn(true);

        mockMvc.perform(post("/api/saveOpeningHours/{cafe_id}", "testCafeId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResult("성공", "저장이 성공하였습니다."))));
    }
}