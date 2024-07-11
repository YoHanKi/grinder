package com.grinder.controller.entity;

import com.grinder.domain.dto.SuccessResult;
import com.grinder.service.CafeRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.grinder.domain.dto.CafeRegisterDTO.*;

@RestController
@RequestMapping("/api/cafe_register")
@RequiredArgsConstructor
public class CafeRegisterController {

    private final CafeRegisterService cafeRegisterService;

    @PostMapping("/newcafe")
    public ResponseEntity<SuccessResult> addCafeRegister(Authentication authentication, @RequestBody CafeRegisterRequestDTO request) {
        if(authentication.isAuthenticated())
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new SuccessResult("NULL", "auth is null"));
        String memberEmail = authentication.getName();
        String registerId = cafeRegisterService.saveCafeRegister(memberEmail, request);
        SuccessResult result = new SuccessResult(memberEmail, registerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
