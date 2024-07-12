package com.grinder.controller.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grinder.domain.dto.MenuDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.MenuService;
import com.grinder.service.MyMenuService;
import com.grinder.service.SellerInfoService;
import com.grinder.service.implement.MenuServiceImpl;
import com.grinder.service.implement.MyMenuServiceImpl;
import com.grinder.service.implement.SellerInfoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {
    @InjectMocks
    MenuController menuController;
    @Mock
    MenuServiceImpl menuService;
    @Mock
    MyMenuServiceImpl myMenuService;
    @Mock
    SellerInfoServiceImpl sellerInfoService;
    @Mock
    Authentication authentication;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(menuController).build();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken("user@example.com", "password", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void deleteMyCafeMenu() throws Exception {
        when(myMenuService.deleteMenu(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(delete("/api/myMenu/{menu_id}", "testMenuId")
                        .param("cafeId", "testCafeId"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("성공", "삭제되었습니다."))));
    }

    @Test
    void saveMyCafeMenu() throws Exception {
        MenuDTO.saveMenuRequest request = new MenuDTO.saveMenuRequest();
        request.setCafeId("testCafeId");
        request.setMenuName("Test Menu");
        request.setMenuPrice("1000");

        when(sellerInfoService.existByMemberAndCafe(anyString(), anyString())).thenReturn(true);
        when(menuService.saveMyCafeMenu(any(MenuDTO.saveMenuRequest.class))).thenReturn(true);

        mockMvc.perform(multipart("/api/menu")
                        .param("cafeId", request.getCafeId())
                        .param("name", request.getMenuName())
                        .param("price", String.valueOf(request.getMenuPrice())))
                .andExpect(status().isCreated())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(new SuccessResult("성공", "저장되었습니다."))));
    }
}