package com.grinder.controller.entity;

import com.grinder.domain.dto.FollowDTO;
import com.grinder.service.implement.FollowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FollowControllerTest {
    @InjectMocks
    private FollowController followController;
    @Mock
    private FollowServiceImpl followService;
    @Mock
    private Authentication authentication;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(followController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("test@test.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
    }

    @Test
    void findAllFollowingSlice() throws Exception {
        // 테스트 데이터 준비
        List<FollowDTO.findAllFollowingResponse> sampleData = new ArrayList<>();
        FollowDTO.findAllFollowingResponse follow = new FollowDTO.findAllFollowingResponse();
        follow.setFollowEmail("test2@test.com");
        sampleData.add(follow);

        // Pageable 인스턴스 생성
        Pageable pageable = PageRequest.of(0, 10);

        // PageImpl을 사용하여 Slice 반환 모방
        Slice<FollowDTO.findAllFollowingResponse> expectedSlice = new PageImpl<>(sampleData, pageable, sampleData.size());

        // Mockito를 사용하여 서비스 메서드가 Slice를 반환하도록 설정
        Mockito.when(followService.findAllFollowingSlice(any(String.class), any(Pageable.class)))
                .thenReturn(expectedSlice);

        // API 호출
        ResultActions resultActions = mockMvc.perform(get("/api/following")
                .param("page", "0")
                .param("size", "10")
                .principal(authentication));

        // 응답 검증
        resultActions.andExpect(status().isOk()).andDo(print());
    }
}