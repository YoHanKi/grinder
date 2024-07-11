package com.grinder.controller.entity;

import com.grinder.domain.dto.HeartDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.domain.entity.Heart;
import com.grinder.service.HeartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/heart")
public class HeartController {
    private final HeartService heartService;

    @PostMapping
    public ResponseEntity<SuccessResult> addHeart(
            @RequestBody HeartDTO.HeartRequestDTO request
    ) {
        String memberEmail = getEmail();
        Heart heart = heartService.addHeart(memberEmail, request);

        if (heart != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult("Add Success", "추천했습니다."));
        } else {
            throw new IllegalArgumentException("예상치 못한 에러가 발생했습니다.");
        }
    }

    @DeleteMapping
    public ResponseEntity<SuccessResult> deleteHeart(
            @RequestBody HeartDTO.HeartRequestDTO request
    ) {
        String memberEmail = getEmail();
        List<Heart> heart = heartService.findHeart(memberEmail, request);
        if (memberEmail.equals(heart.get(0).getMember().getEmail())) {
            heartService.deleteHeart(memberEmail, request);
            if (!heartService.isHeart(memberEmail, request)) {
                return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResult("Delete Success", "추천 해제했습니다."));
            } else {
                throw new IllegalArgumentException("예상치 못한 에러가 발생했습니다.");
            }
        } else {    // 403에러 (회원 불일치)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  Optional.ofNullable(authentication.getName()).orElse(null);
    }
}
