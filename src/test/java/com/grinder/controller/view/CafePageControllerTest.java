package com.grinder.controller.view;

import com.grinder.domain.dto.CafeDTO.CafeResponseDTO;
import com.grinder.domain.dto.CafeSummaryDTO;
import com.grinder.domain.dto.MenuDTO;
import com.grinder.service.CafeService;
import com.grinder.service.CafeSummaryService;
import com.grinder.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CafePageControllerTest {

    @InjectMocks
    private CafePageController cafePageController;

    @Mock
    private CafeService cafeService;

    @Mock
    private MenuService menuService;

    @Mock
    private CafeSummaryService cafeSummaryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(cafePageController).build();
    }

    @Test
    void getAddCafeForm() throws Exception {
        mockMvc.perform(get("/cafe/newcafe"))
                .andExpect(status().isOk())
                .andExpect(view().name("addCafeForm"));
    }

    @Test
    void getCafeInfo() throws Exception {
        CafeResponseDTO cafeResponseDTO = new CafeResponseDTO();
        when(cafeService.getCafeInfo(anyString())).thenReturn(cafeResponseDTO);

        mockMvc.perform(get("/cafe/{cafeId}", "testCafeId"))
                .andExpect(status().isOk())
                .andExpect(view().name("cafeInfo"))
                .andExpect(model().attributeExists("cafeInfo"));
    }

    @Test
    void applyCafeSeller() throws Exception {
        mockMvc.perform(get("/cafe/seller_apply/{cafeId}", "testCafeId"))
                .andExpect(status().isOk())
                .andExpect(view().name("sellerApplicationForm"))
                .andExpect(model().attributeExists("cafeId"));
    }

    @Test
    void addCafeInfo() throws Exception {
        mockMvc.perform(get("/cafe/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("addCafeForm"));
    }

    @Test
    void getCafeMenu() throws Exception {
        List<MenuDTO.findAllMenuResponse> menuList = List.of(new MenuDTO.findAllMenuResponse());
        when(menuService.findAllMenusByCafeId(anyString())).thenReturn(menuList);

        mockMvc.perform(get("/cafe/{cafeId}/menu", "testCafeId"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/menuCard :: cafeInfoMenuCard"))
                .andExpect(model().attributeExists("menuList"));
    }

    @Test
    void findCafeSummary() throws Exception {
        CafeSummaryDTO.CafeSummaryResponse response = new CafeSummaryDTO.CafeSummaryResponse();
        when(cafeSummaryService.findCafeSummary(anyString())).thenReturn(response);

        mockMvc.perform(get("/cafe/{cafeId}/cafe_summary", "testCafeId"))
                .andExpect(status().isOk())
                .andExpect(view().name("components/cafeSummary :: cafeSummary"))
                .andExpect(model().attributeExists("summary"));
    }
}