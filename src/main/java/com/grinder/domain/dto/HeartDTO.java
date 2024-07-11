package com.grinder.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HeartDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HeartRequestDTO {
        private String contentId;
        private String contentType;
    }
}
