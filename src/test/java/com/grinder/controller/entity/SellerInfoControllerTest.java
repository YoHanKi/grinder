package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.SellerInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SellerInfoControllerTest {

    @InjectMocks
    private SellerInfoController sellerInfoController;

    @Mock
    private SellerInfoService sellerInfoService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(sellerInfoController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void deleteSellerInfo() throws Exception {
        doNothing().when(sellerInfoService).deleteSellerInfo(anyLong());

        mockMvc.perform(delete("/api/seller_info/{seller_info_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessResult("Delete seller_info", "판매자 정보가 삭제되었습니다."))));
    }
}