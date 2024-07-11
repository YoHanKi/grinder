package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.BlacklistDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.implement.BlacklistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BlacklistControllerTest {
    @InjectMocks
    BlacklistController blacklistController;
    @Mock
    BlacklistServiceImpl blacklistService;
    @Mock
    private Authentication authentication;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(blacklistController).build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("user@example.com"); // 필요한 메소드를 목킹

    }


    @Test
    void findAllBlacklist() throws Exception {
        List<BlacklistDTO.findAllResponse> expectedResponse = new ArrayList<>();
        BlacklistDTO.findAllResponse thing = new BlacklistDTO.findAllResponse();
        thing.setBlockedEmail("test@example.com");
        expectedResponse.add(thing);

        Mockito.doReturn(expectedResponse)
                .when(blacklistService).findAllBlacklist(anyString());

        mockMvc.perform(get("/api/blacklist")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));
    }

    @Test
    void addBlacklist() throws Exception {
        BlacklistDTO.AddRequest addRequest = new BlacklistDTO.AddRequest();
        addRequest.setBlockedMemberEmail("blocked@example.com");

        when(blacklistService.addBlacklist(any(BlacklistDTO.AddRequest.class), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/blacklist/{email}", "blocked@example.com")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Add Success", "추가되었습니다."))));
    }

    @Test
    void deleteBlacklist() throws Exception {
        when(blacklistService.deleteBlacklist(anyLong(), anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/blacklist/{id}", 1L)
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Delete Success", "삭제되었습니다."))));
    }
}