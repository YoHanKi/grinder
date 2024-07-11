package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.HeartDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.service.implement.HeartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HeartControllerTest {
    @InjectMocks
    HeartController heartController;
    @Mock
    HeartServiceImpl heartService;
    @Mock
    Authentication authentication;

    MockMvc mockMvc;
    private Member member;
    private Cafe cafe;
    private Feed feed;
    private Comment comment;
    private Heart heart;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(heartController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        member = Member.builder().email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();

        cafe = Cafe.builder().name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();

        feed = Feed.builder().member(member).cafe(cafe).content("Great coffee and atmosphere!").isVisible(true).grade(5).build();

        comment = Comment.builder().commentId("testId").member(member).feed(feed).content("good").build();

        heart = Heart.builder().heartId("testHeartId").member(member).contentId("testContentId").contentType(ContentType.FEED).build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("member1@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("member1@example.com");
    }

    @Test
    void addHeart() throws Exception {
        HeartDTO.HeartRequestDTO requestDTO = new HeartDTO.HeartRequestDTO();
        requestDTO.setContentId("testContentId");
        requestDTO.setContentType("FEED");

        when(heartService.addHeart(anyString(), any(HeartDTO.HeartRequestDTO.class)))
                .thenReturn(heart);

        mockMvc.perform(post("/heart")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(requestDTO))
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Add Success", "추천했습니다."))));
    }

    @Test
    void deleteHeart() throws Exception {
        HeartDTO.HeartRequestDTO requestDTO = new HeartDTO.HeartRequestDTO();
        requestDTO.setContentId("testContentId");
        requestDTO.setContentType("FEED");

        List<Heart> hearts = new ArrayList<>();
        hearts.add(heart);

        when(heartService.findHeart(anyString(), any(HeartDTO.HeartRequestDTO.class)))
                .thenReturn(hearts);
        when(heartService.isHeart(anyString(), any(HeartDTO.HeartRequestDTO.class)))
                .thenReturn(false);

        mockMvc.perform(delete("/heart")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(requestDTO))
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Delete Success", "추천 해제했습니다."))));
    }
}