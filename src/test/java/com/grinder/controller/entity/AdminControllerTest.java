package com.grinder.controller.entity;

import com.grinder.domain.dto.*;
import com.grinder.domain.entity.Cafe;
import com.grinder.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @Spy
    @InjectMocks
    AdminController adminController;
    @Mock
    CafeService cafeService;
    @Mock
    CafeRegisterService cafeRegisterService;
    @Mock
    CommentService commentService;
    @Mock
    FeedService feedService;
    @Mock
    MemberService memberService;
    @Mock
    ReportService reportService;
    @Mock
    SellerApplyService sellerApplyService;
    @Mock
    SellerInfoService sellerInfoService;


    MockMvc mockMvc;
    Cafe cafe;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adminController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        cafe = Cafe.builder().name("name").address("address").phoneNum("12345").build();
    }

    @DisplayName("신규 카페 등록")
    @Test
    void testAddCafe() throws Exception {
        String registerId = UUID.randomUUID().toString();
        doNothing().when(cafeService).saveCafe(anyString());
        doNothing().when(cafeRegisterService).deleteCafeRegister(anyString());

        mockMvc.perform(post("/admin/api/cafe/" + registerId))
                .andExpect(status().isCreated());
    }

    @DisplayName("카페 검색")
    @Test
    void testSearchCafeByAdmin() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(cafeService.searchCafeByAdmin(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        mockMvc.perform(get("/admin/api/cafe")
                        .param("keyword", "test")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @DisplayName("카페 등록 신청 조회")
    @Test
    void testFindAllCafeRegisters() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(cafeRegisterService.FindAllCafeRegisters(any())).thenReturn(new SliceImpl<>(Collections.emptyList(), pageable, false));

        mockMvc.perform(get("/admin/api/cafe_register")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @DisplayName("댓글 조회")
    @Test
    void testFindCommentForAdmin() throws Exception {
        when(commentService.findCommentForAdmin(anyString())).thenReturn(new CommentDTO.FindCommentDTO());

        mockMvc.perform(get("/admin/comment/{comment_id}", "testCommentId"))
                .andExpect(status().isOk());
    }

    @DisplayName("피드 조회")
    @Test
    void testFindFeedForAdmin() throws Exception {
        when(feedService.findFeedForAdmin(anyString())).thenReturn(new FeedDTO.FindFeedDTO());

        mockMvc.perform(get("/admin/feed/{feed_id}", "testFeedId"))
                .andExpect(status().isOk());
    }

    @DisplayName("회원 역할 업데이트")
    @Test
    void testUpdateMemberRole() throws Exception {
        doNothing().when(memberService).updateMemberRole(anyString());

        mockMvc.perform(put("/admin/api/member/{memberId}/role", "testMemberId"))
                .andExpect(status().isOk());
    }

    @DisplayName("회원 삭제")
    @Test
    void testDeleteMember() throws Exception {
        when(memberService.deleteMember(anyString())).thenReturn(true);

        mockMvc.perform(delete("/admin/api/member/{memberId}", "testMemberId"))
                .andExpect(status().isOk());
    }

    @DisplayName("회원 복구")
    @Test
    void testRecoverMember() throws Exception {
        when(memberService.recoverMember(anyString())).thenReturn(true);

        mockMvc.perform(put("/admin/api/member/{memberId}/recovery", "testMemberId"))
                .andExpect(status().isOk());
    }

    @DisplayName("회원 검색")
    @Test
    void testSearchMemberByNicknameAndRole() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(memberService.searchMemberSlice(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        mockMvc.perform(get("/admin/api/member/search")
                        .param("nickname", "test")
                        .param("role", "USER")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @DisplayName("신고 삭제")
    @Test
    void testDeleteReport() throws Exception {
        doNothing().when(reportService).deleteReport(anyString());

        mockMvc.perform(delete("/admin/api/report/{reportId}", "testReportId"))
                .andExpect(status().isOk());
    }

    @DisplayName("신고된 컨텐츠 삭제")
    @Test
    void testDeleteContent() throws Exception {
        doNothing().when(reportService).deleteContent(anyString());

        mockMvc.perform(delete("/admin/api/report/{reportId}/accepted", "testReportId"))
                .andExpect(status().isOk());
    }

    @DisplayName("신고 검색")
    @Test
    void testSearchReportByContentAndType() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(reportService.searchReportByContentAndType(anyString(), anyString(), any())).thenReturn(new SliceImpl<>(Collections.emptyList(), pageable, false));

        mockMvc.perform(get("/admin/api/report/search")
                        .param("keyword", "test")
                        .param("contentType", "FEED")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @DisplayName("판매자 신청 조회")
    @Test
    void testFindAllSellerApplies() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        when(sellerApplyService.findAllSellerApplies(any())).thenReturn(new SliceImpl<>(Collections.emptyList(), pageable, false));

        mockMvc.perform(get("/admin/api/seller_apply")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());
    }

    @DisplayName("판매자 신청 삭제")
    @Test
    void testDeleteSellerApply() throws Exception {
        doNothing().when(sellerApplyService).deleteSellerApply(anyString());

        mockMvc.perform(delete("/admin/api/seller_apply/{applyId}", "testApplyId"))
                .andExpect(status().isOk());
    }

    @DisplayName("판매자 정보 저장")
    @Test
    void testSaveSellerInfo() throws Exception {
        doNothing().when(sellerInfoService).saveSellerInfo(anyString());

        mockMvc.perform(post("/admin/api/seller_info/{applyId}", "testApplyId"))
                .andExpect(status().isCreated());
    }
}