package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.Role;
import com.grinder.service.implement.FeedServiceImpl;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FeedControllerTest {
    @InjectMocks
    FeedController feedController;
    @Mock
    FeedServiceImpl feedService;
    @Mock
    Authentication authentication;

    MockMvc mockMvc;
    private Member member;
    private Feed feed;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(feedController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        member = Member.builder()
                .email("member1@example.com")
                .nickname("user1")
                .password("password1")
                .role(Role.MEMBER)
                .phoneNum("1234567890")
                .build();

        feed = Feed.builder()
                .feedId("testFeedId")
                .member(member)
                .content("Great coffee and atmosphere!")
                .isVisible(true)
                .grade(5)
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("member1@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("member1@example.com");
    }

    @Test
    void addFeed() throws Exception {
        FeedDTO.FeedRequestDTO requestDTO = new FeedDTO.FeedRequestDTO();
        requestDTO.setContent("Great feed content!");

        when(feedService.saveFeed(any(FeedDTO.FeedRequestDTO.class), anyString(), anyList()))
                .thenReturn(feed);

        mockMvc.perform(multipart("/feed/newfeed")
                        .file("imageList", new byte[1])
                        .param("content", requestDTO.getContent())
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Add Success", "추가되었습니다."))));
    }

    @Test
    void updateFeed() throws Exception {
        FeedDTO.FeedRequestDTO requestDTO = new FeedDTO.FeedRequestDTO();
        requestDTO.setContent("Updated feed content!");

        when(feedService.findFeed(anyString())).thenReturn(feed);
        when(feedService.updateFeed(anyString(), any(FeedDTO.FeedRequestDTO.class), anyList()))
                .thenReturn(feed);

        mockMvc.perform(multipart("/feed/{feed_id}", "testFeedId")
                        .file("imageList", new byte[1])
                        .param("content", requestDTO.getContent())
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Update Success", "수정되었습니다."))));
    }

    @Test
    void deleteFeed() throws Exception {
        Feed feed = Feed.builder()
                .feedId("testFeedId")
                .member(member)
                .content("Great coffee and atmosphere!")
                .isVisible(false)
                .grade(5)
                .build();

        when(feedService.findFeed(anyString())).thenReturn(feed);

        mockMvc.perform(delete("/feed/{feed_id}", "testFeedId")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Delete Success", "삭제되었습니다."))));
    }
}