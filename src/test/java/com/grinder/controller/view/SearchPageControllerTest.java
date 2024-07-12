package com.grinder.controller.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SearchPageControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SearchPageController searchPageController = new SearchPageController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(searchPageController).build();
    }

    @Test
    void viewSearchPage() throws Exception {
        // Arrange
        String category = "testCategory";
        String query = "testQuery";

        // Act & Assert
        mockMvc.perform(get("/search")
                        .param("category", category)
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(view().name("searchPage"))
                .andExpect(model().attribute("category", category))
                .andExpect(model().attribute("query", query));
    }
}