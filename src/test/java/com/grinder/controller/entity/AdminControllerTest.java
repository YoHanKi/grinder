package com.grinder.controller.entity;

import com.grinder.domain.dto.CafeRegisterDTO;
import com.grinder.domain.entity.CafeRegister;
import com.grinder.domain.entity.Member;
import com.grinder.service.implement.CafeRegisterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    @InjectMocks
    AdminController adminController;

    @Mock
    CafeRegisterServiceImpl cafeRegisterService;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    @DisplayName("신규 장소 등록신청 삭제")
    @Test
    void testDenyCafeRegister() throws Exception{
        List<CafeRegisterDTO.FindCafeRegisterDTO> registerDTOList = new ArrayList<>();
        String cafeRegisterId = "";
        for (int i = 0; i < 3; i++) {
            cafeRegisterId = UUID.randomUUID().toString();
            CafeRegisterDTO.FindCafeRegisterDTO temp = new CafeRegisterDTO.FindCafeRegisterDTO();
            temp.setRegisterId(cafeRegisterId);
            temp.setCafeName("test");
            temp.setAddress("address");
            temp.setNickname("name");
            temp.setCafeName("cafe");
            registerDTOList.add(temp);
        }


        doNothing().when(cafeRegisterService).deleteCafeRegister(anyString());


        mockMvc.perform(delete("/admin/api/cafe_register/" + cafeRegisterId))
                .andExpect(status().isOk());

    }
}