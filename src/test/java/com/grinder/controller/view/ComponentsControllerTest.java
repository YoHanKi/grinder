package com.grinder.controller.view;

import com.grinder.controller.ExControllerAdvice;
import com.grinder.domain.dto.*;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.MenuType;
import com.grinder.domain.enums.Role;
import com.grinder.domain.enums.TagName;
import com.grinder.exception.NoMoreContentException;
import com.grinder.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
class ComponentsControllerTest {

    @InjectMocks
    private ComponentsController componentsController;

    @Mock
    private MemberService memberService;
    @Mock
    private FollowService followService;
    @Mock
    private BlacklistService blacklistService;
    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private SellerInfoService sellerInfoService;
    @Mock
    private FeedService feedService;
    @Mock
    private MyMenuService myMenuService;
    @Mock
    private MessageService messageService;
    @Mock
    private CafeService cafeService;
    @Mock
    private ImageService imageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(componentsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new ExControllerAdvice())
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("test@test.com", "password", List.of());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void getHeader() throws Exception {
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(any())).thenReturn(member);
        when(messageService.existNonCheckMessage(any())).thenReturn(true);
        when(messageService.findAllByEmail(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/get-header"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/header :: headers"));
    }

    @Test
    void getAlanMessage() throws Exception {
        when(messageService.findAllByEmail(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/get-alan"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/alanTab :: alan_tab"));
    }

    @Test
    void getFollower() throws Exception {
        List<FollowDTO.findAllFollowerResponse> followerResponses = new ArrayList<>();
        PageRequest pageable = PageRequest.of(0, 10);
        when(followService.findAllFollowerSlice(any(), any())).thenReturn(new SliceImpl<>(followerResponses, pageable, false));

        mockMvc.perform(get("/get-follower"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/followerList :: followList(title='follower')"));
    }

    @Test
    void getFollowing() throws Exception {
        List<FollowDTO.findAllFollowingResponse> followingResponses = new ArrayList<>();
        PageRequest pageable = PageRequest.of(0, 10);
        when(followService.findAllFollowingSlice(any(), any())).thenReturn(new SliceImpl<>(followingResponses, pageable, false));

        mockMvc.perform(get("/get-following"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/followerList :: followList(title='following')"));
    }

    @Test
    void getBlacklist() throws Exception {
        when(blacklistService.findAllBlacklist(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/get-blacklist"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/blacklist :: blackList"));
    }

    @Test
    void getBookmark() throws Exception {
        List<BookmarkDTO.findAllResponse> bookmarks = new ArrayList<>();
        PageRequest pageable = PageRequest.of(0, 10);
        when(bookmarkService.findAllBookmarksSlice(any(), any())).thenReturn(new SliceImpl<>(bookmarks, pageable, false));

        mockMvc.perform(get("/get-bookmark"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/bookmark :: bookmarkList"));
    }

    @Test
    void getMyCafe() throws Exception {
        when(sellerInfoService.findAllSellerInfoByEmail(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/get-mycafe"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/myCafeList :: myCafeList"));
    }

    @Test
    void getOpening() throws Exception {
        mockMvc.perform(get("/get-opening"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/updateCafeInfo :: updateCafeInfo"));
    }

    @Test
    void getMyMenu() throws Exception {
        when(myMenuService.findAllMenuWithImage(any(), any())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/get-mymenu/testCafeId"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/myCafeMenu :: myCafeMenu"));
    }

    @Test
    void addMenuTab() throws Exception {
        mockMvc.perform(get("/get-addpage"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/menuInfo :: addMenu"));
    }

    @Test
    void getFeed() throws Exception {
        PageRequest pageable = PageRequest.of(0, 20);
        when(feedService.findRecentFeedWithImage(any(), any())).thenReturn(new SliceImpl<>(new ArrayList<>(), pageable, true));
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(anyString())).thenReturn(member);

        mockMvc.perform(get("/get-feed"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/feed"));
    }

    @Test
    void getCafeCard() throws Exception {
        PageRequest pageable = PageRequest.of(0, 6);
        when(cafeService.searchCafes(any(), any())).thenReturn(new SliceImpl<>(new ArrayList<>(), pageable, true));

        mockMvc.perform(get("/get-cafeCard")
                        .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/cafeCard"));
    }

    @Test
    void getSearchFeed() throws Exception {
        PageRequest pageable = PageRequest.of(0, 5);
        when(feedService.searchFeed(any(), any(), any())).thenReturn(new SliceImpl<>(new ArrayList<>(), pageable, true));
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(any())).thenReturn(member);
        mockMvc.perform(get("/get-search-feed")
                        .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/feed"));
    }

    @Test
    void getSearchFeed_예외() throws Exception {
        PageRequest pageable = PageRequest.of(0, 5);
        when(feedService.searchFeed(any(), any(), any())).thenReturn(new SliceImpl<>(new ArrayList<>(), pageable, false));
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(any())).thenReturn(member);

        mockMvc.perform(get("/get-search-feed")
                        .param("query", "test"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMyFeed() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        when(feedService.findMyPageFeedWithImage(any(), any(), any())).thenReturn(new SliceImpl<>(new ArrayList<>(), pageable, true));
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(any())).thenReturn(member);

        mockMvc.perform(get("/get-myFeed/test@test.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/feed"));
    }

    @Test
    void getCafeFeed() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        when(feedService.findCafeFeedWithImage(any(), any(), any())).thenReturn(new SliceImpl<>(new ArrayList<>(), pageable, true));
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(any())).thenReturn(member);

        mockMvc.perform(get("/get-cafeFeed/testCafeId"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/feed"));
    }

    @Test
    void getSearchMember() throws Exception {
        PageRequest pageable = PageRequest.of(0, 10);
        when(memberService.searchMember(any(), any(), any())).thenReturn(new SliceImpl<>(new ArrayList<>(), pageable, true));
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(any())).thenReturn(member);

        mockMvc.perform(get("/get-search-member")
                        .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/followerList :: followList(title='search')"));
    }

    @Test
    void getMainCafeCard() throws Exception {
        when(cafeService.weekTop3Cafe()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/get-main-card"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/cafeCard :: cafeCards"));
    }

    @Test
    void getFeedComment() throws Exception {
        when(feedService.findFeedForComment(any(), any())).thenReturn(new ArrayList<>());
        Member member = Member.builder().memberId("test").email("test").nickname("test").phoneNum("test").role(Role.ADMIN).isDeleted(false).build();
        when(memberService.findMemberByEmail(any())).thenReturn(member);

        mockMvc.perform(get("/get-feed-comment/testFeedId"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/comment :: comment_update"));
    }
}