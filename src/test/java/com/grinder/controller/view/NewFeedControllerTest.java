package com.grinder.controller.view;

import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Image;
import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Tag;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.domain.enums.TagName;
import com.grinder.service.FeedService;
import com.grinder.service.ImageService;
import com.grinder.service.MemberService;
import com.grinder.service.TagService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class NewFeedControllerTest {

    @InjectMocks
    NewFeedController newFeedController;

    @Mock
    FeedService feedService;

    @Mock
    ImageService imageService;

    @Mock
    TagService tagService;

    @Mock
    MemberService memberService;

    @Mock
    Authentication authentication;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(newFeedController).build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("test@test.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
    }

    @Test
    void newFeed_withFeedId() throws Exception {
        // Arrange
        MemberDTO.FindMemberDTO memberDTO = new MemberDTO.FindMemberDTO();
        memberDTO.setEmail("test@test.com");
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(anyString())).thenReturn(member);

        Feed feed = new Feed();
        when(feedService.findFeed(anyString())).thenReturn(feed);

        List<Image> imageList = new ArrayList<>();
        when(imageService.findAllImage(anyString(), any(ContentType.class))).thenReturn(imageList);

        List<Tag> tagList = new ArrayList<>();
        when(tagService.findAllTag(anyString())).thenReturn(tagList);

        // Act & Assert
        mockMvc.perform(get("/feed/newfeed").param("feedId", "testFeedId"))
                .andExpect(status().isOk())
                .andExpect(view().name("feedWriteForm"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("newfeed"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(model().attribute("tagList", TagName.values()));
    }

    @Test
    void newFeed_withoutFeedId() throws Exception {
        // Arrange
        MemberDTO.FindMemberDTO memberDTO = new MemberDTO.FindMemberDTO();
        memberDTO.setEmail("test@test.com");
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(anyString())).thenReturn(member);

        // Act & Assert
        mockMvc.perform(get("/feed/newfeed"))
                .andExpect(status().isOk())
                .andExpect(view().name("feedWriteForm"))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("newfeed"))
                .andExpect(model().attributeExists("tagList"))
                .andExpect(model().attribute("tagList", TagName.values()));
    }
}