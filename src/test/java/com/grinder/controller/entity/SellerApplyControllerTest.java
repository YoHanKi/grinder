package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.security.dto.CustomUserDetails;
import com.grinder.service.SellerApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SellerApplyControllerTest {

    @InjectMocks
    private SellerApplyController sellerApplyController;

    @Mock
    private SellerApplyService sellerApplyService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(sellerApplyController).build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("member1@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("member1@example.com");
    }

    @Test
    void saveSellerApply() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes());

        doNothing().when(sellerApplyService).saveSellerApply(anyString(), anyString(), any(MockMultipartFile.class));

        mockMvc.perform(multipart("/api/seller_apply/{cafeId}", "testCafeId")
                        .file(file)
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Create seller apply", "판매자 신청이 완료되었습니다."))));
    }
}