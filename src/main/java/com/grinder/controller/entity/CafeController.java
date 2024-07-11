package com.grinder.controller.entity;

import com.grinder.domain.dto.CafeDTO;
import com.grinder.domain.dto.CafeDTO.CafeResponseDTO;
import com.grinder.domain.dto.SuccessResult;
import com.grinder.domain.entity.Cafe;
import com.grinder.service.CafeRegisterService;
import com.grinder.service.CafeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cafe")
@RequiredArgsConstructor
public class CafeController {

    private final CafeService cafeService;

    @GetMapping("/search-cafe")
    public ResponseEntity<List<CafeResponseDTO>> searchCafes(@RequestParam String query) {
        if (query == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        try {
            List<Cafe> cafes = cafeService.findCafeList(query);
            List<CafeDTO.CafeResponseDTO> cafeDTOs = cafes.stream()
                    .map(cafe -> cafeService.getCafeInfo(cafe.getCafeId()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(cafeDTOs);
        } catch (Exception e) {
            // 예외 발생 시 로그를 출력하고 500 Internal Server Error 응답 반환
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
