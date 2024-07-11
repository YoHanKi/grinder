package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.ImageDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.AwsS3Service;
import com.grinder.service.ImageService;
import com.grinder.service.MemberService;
import com.grinder.service.SellerInfoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {
    @InjectMocks
    ImageController imageController;
    @Mock
    ImageService imageService;
    @Mock
    SellerInfoService sellerInfoService;
    @Mock
    MemberService memberService;
    @Mock
    AwsS3Service awsS3Service;
    @Mock
    Authentication authentication;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(imageController)
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void saveAndDeleteImage() throws Exception {
        ImageDTO.UpdateRequest request = new ImageDTO.UpdateRequest();
        request.setCafeId("testCafeId");
        request.setImage((MultipartFile) new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]));

        when(sellerInfoService.existByMemberAndCafe(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(multipart("/api/image")
                        .file("image", request.getImage().getBytes())
                        .param("cafeId", request.getCafeId()))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("성공", "저장되었습니다."))));
    }

    @Test
    void deleteCafeImage() throws Exception {
        when(sellerInfoService.existByMemberAndCafe(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/image/{cafeId}", "testCafeId"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("성공", "삭제되었습니다."))));
    }

    @Test
    void deleteImage() throws Exception {
        when(memberService.existEmail(anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/image"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("성공", "삭제되었습니다."))));
    }
}