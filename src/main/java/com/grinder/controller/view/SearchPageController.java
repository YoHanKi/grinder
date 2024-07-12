package com.grinder.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchPageController {

    @GetMapping("/search")
    public String viewSearchPage(@RequestParam String category, @RequestParam String query, Model model) {
        model.addAttribute("query", query);
        model.addAttribute("category", category);
        return "searchPage";
    }
}
