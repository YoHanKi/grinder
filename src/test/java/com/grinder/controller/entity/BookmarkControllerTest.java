package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.BookmarkDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.implement.BookmarkServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookmarkControllerTest {
    @InjectMocks
    private BookmarkController bookmarkController;
    @Mock
    private BookmarkServiceImpl bookmarkService;
    @Mock
    private Authentication authentication;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(bookmarkController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("test@test.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
    }

    @Test
    void findAllBookmarksSlice() throws Exception {
        // 샘플 데이터 준비
        BookmarkDTO test = new BookmarkDTO();
        List<BookmarkDTO.findAllResponse> sampleData = new ArrayList<>();
        BookmarkDTO.findAllResponse bookmark = new BookmarkDTO.findAllResponse();
        bookmark.setCafeName("스타벅스");
        sampleData.add(bookmark);

        // Pageable 인스턴스 생성
        Pageable pageable = PageRequest.of(0, 10);

        // PageImpl을 사용하여 Slice 반환 모방
        Slice<BookmarkDTO.findAllResponse> expectedSlice = new PageImpl<>(sampleData, pageable, sampleData.size());

        // Mockito를 사용하여 서비스 메서드가 Slice를 반환하도록 설정
        Mockito.when(bookmarkService.findAllBookmarksSlice(anyString(), any(Pageable.class)))
                .thenReturn(expectedSlice);

        // API 호출
        ResultActions resultActions = mockMvc.perform(get("/api/bookmark")
                .param("page", "0")
                .param("size", "10")
                .principal(authentication));

        // 응답 검증
        resultActions.andExpect(status().isOk()).andExpect(content().json(new ObjectMapper().writeValueAsString(sampleData))).andDo(print());
    }

    @Test
    void isBookmarked() throws Exception {
        // Mockito를 사용하여 서비스 메서드가 true를 반환하도록 설정
        when(bookmarkService.existsBookmarkByEmailAndCafeId(anyString(), anyString())).thenReturn(true);

        // API 호출
        mockMvc.perform(get("/api/bookmark/{cafeId}", "testCafeId")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void addBookmark() throws Exception {
        // Mockito를 사용하여 서비스 메서드가 true를 반환하도록 설정
        when(bookmarkService.addBookmark(anyString(), anyString())).thenReturn(true);

        // API 호출
        mockMvc.perform(post("/api/bookmark/{cafeId}", "testCafeId")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Add Success", "추가되었습니다."))));
    }

    @Test
    void deleteBookmark() throws Exception {
        // Mockito를 사용하여 서비스 메서드가 true를 반환하도록 설정
        when(bookmarkService.deleteBookmark(anyString(), anyString())).thenReturn(true);

        // API 호출
        mockMvc.perform(delete("/api/bookmark/{cafeId}", "testCafeId")
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("Delete Success", "삭제되었습니다."))));
    }
}