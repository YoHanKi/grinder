package com.grinder.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminPageController {

    @GetMapping("/admin")
    public String viewAdminPage() {
        return "adminPage";
    }
}
