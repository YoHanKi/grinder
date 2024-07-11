package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.CafeSummaryDTO;
import com.grinder.domain.dto.CommentDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Comment;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.Role;
import com.grinder.service.CommentService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    @InjectMocks
    CommentController commentController;
    @Mock
    CommentService commentService;
    @Mock
    Authentication authentication;

    MockMvc mockMvc;
    private Member member;
    private Cafe cafe;
    private Feed feed;
    private Comment comment;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(commentController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        member = Member.builder().email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        cafe = Cafe.builder().name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        feed = Feed.builder().member(member).cafe(cafe).content("Great coffee and atmosphere!").isVisible(true).grade(5).build();
        comment = Comment.builder().commentId("testId").member(member).feed(feed).content("good").build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("member1@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("member1@example.com");
    }
    @Test
    void addComment() throws Exception {
        CommentDTO.CommentRequestDTO requestDTO = new CommentDTO.CommentRequestDTO();
        requestDTO.setContent("Great comment!");

        when(commentService.saveComment(any(CommentDTO.CommentRequestDTO.class), anyString(), anyString()))
                .thenReturn(comment);

        mockMvc.perform(post("/comment/{feed_id}/newcomment", "testFeedId")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(requestDTO))
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Add Success", "추가되었습니다."))));
    }

    @Test
    void updateComment() throws Exception {
        CommentDTO.CommentRequestDTO requestDTO = new CommentDTO.CommentRequestDTO();
        requestDTO.setContent("Updated comment!");

        when(commentService.findComment(anyString())).thenReturn(comment);
        when(commentService.updateComment(anyString(), anyString())).thenReturn(comment);

        mockMvc.perform(put("/comment/{feed_id}/{comment_id}", "testFeedId", "testCommentId")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(requestDTO))
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Update Success", "수정되었습니다."))));
    }

    @Test
    void deleteComment() throws Exception {
        Comment comment = Comment.builder().commentId("testId").member(member).feed(feed).content("good").isVisible(false).build();
        when(commentService.findComment(anyString())).thenReturn(comment);

        mockMvc.perform(delete("/comment/{feed_id}/{comment_id}", "testFeedId", "testCommentId")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Delete Success", "삭제되었습니다."))));
    }
}