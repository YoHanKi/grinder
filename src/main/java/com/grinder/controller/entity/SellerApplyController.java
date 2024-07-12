package com.grinder.controller.entity;

import com.grinder.domain.dto.SuccessResult;
import com.grinder.security.dto.CustomUserDetails;
import com.grinder.service.SellerApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.grinder.domain.dto.SellerApplyDTO.*;

@RestController
@RequestMapping("/api/seller_apply")
@RequiredArgsConstructor
public class SellerApplyController {

    private final SellerApplyService sellerApplyService;

    @PostMapping("/{cafeId}")
    public ResponseEntity<SuccessResult> saveSellerApply(@PathVariable String cafeId, @RequestPart(value = "file")MultipartFile file, Authentication authentication) {
        String memberId = getEmail();

        sellerApplyService.saveSellerApply(memberId, cafeId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult("Create seller apply", "판매자 신청이 완료되었습니다."));
    }

    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  Optional.ofNullable(authentication.getName()).orElse(null);
    }
}
