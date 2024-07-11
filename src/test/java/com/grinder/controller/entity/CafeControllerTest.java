package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.CafeDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.service.CafeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CafeControllerTest {
    @InjectMocks
    CafeController cafeController;
    @Mock
    Authentication authentication;
    @Mock
    CafeService cafeService;

    MockMvc mockMvc;

    Cafe cafe;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(cafeController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        cafe = Cafe.builder().cafeId("test").name("name").address("address").phoneNum("12345").build();
    }

    @Test
    void searchCafes() throws Exception {
        List<Cafe> cafes = new ArrayList<>();
        cafes.add(cafe);

        CafeDTO.CafeResponseDTO cafeResponseDTO = new CafeDTO.CafeResponseDTO();
        cafeResponseDTO.setName("name");
        cafeResponseDTO.setAddress("address");
        cafeResponseDTO.setPhoneNum("12345");

        List<CafeDTO.CafeResponseDTO> cafeResponseDTOList = new ArrayList<>();
        cafeResponseDTOList.add(cafeResponseDTO);

        when(cafeService.findCafeList(anyString())).thenReturn(cafes);
        when(cafeService.getCafeInfo(anyString())).thenReturn(cafeResponseDTO);

        mockMvc.perform(get("/api/cafe/search-cafe")
                        .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(cafeResponseDTOList)));
    }
}