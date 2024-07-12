package com.grinder.controller.view;

import com.grinder.domain.dto.CafeDTO;
import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.service.*;
import com.grinder.service.implement.CafeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MyPageControllerTest {

    @InjectMocks
    MyPageController myPageController;

    @Mock
    MemberService memberService;

    @Mock
    CafeServiceImpl cafeService;

    @Mock
    SellerInfoService sellerInfoService;

    @Mock
    ImageService imageService;

    @Mock
    FollowService followService;

    @Mock
    Authentication authentication;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(myPageController).build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("test@test.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
    }

    @Test
    void viewMyPage() throws Exception {
        // Arrange
        MemberDTO.FindMemberAndImageDTO memberDTO = new MemberDTO.FindMemberAndImageDTO();
        memberDTO.setEmail("test@test.com");
        when(memberService.findMemberAndImageById(anyString())).thenReturn(memberDTO);
        when(followService.existFollow(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/mypage/test"))
                .andExpect(status().isOk())
                .andExpect(view().name("myPage"))
                .andExpect(model().attributeExists("isFollow"))
                .andExpect(model().attributeExists("myPageMember"))
                .andExpect(model().attributeExists("connectEmail"))
                .andExpect(model().attribute("connectEmail", "test@test.com"));
    }

    @Test
    void viewMyCafe() throws Exception {
        // Arrange
        Cafe cafe = new Cafe();
        when(cafeService.findCafeById(anyString())).thenReturn(cafe);
        when(sellerInfoService.existByMemberAndCafe(anyString(), anyString())).thenReturn(true);
        when(imageService.findImageUrlByContentId(anyString())).thenReturn("testImageUrl");

        // Act & Assert
        mockMvc.perform(get("/mycafe/testCafe"))
                .andExpect(status().isOk())
                .andExpect(view().name("myCafePage"))
                .andExpect(model().attributeExists("myCafe"));
    }

    @Test
    void modifyMyImage() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/myImage"))
                .andExpect(status().isOk())
                .andExpect(view().name("myImageUpdate"));
    }

    @Test
    void modifyCafeImage() throws Exception {
        // Arrange
        when(sellerInfoService.existByMemberAndCafe(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/myCafeImage/testCafe"))
                .andExpect(status().isOk())
                .andExpect(view().name("myImageUpdate"))
                .andExpect(model().attributeExists("cafeId"))
                .andExpect(model().attribute("cafeId", "testCafe"));
    }
}