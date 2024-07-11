package com.grinder.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;

@Data
@AllArgsConstructor
public class SuccessResult {
    private String code;
    private String message;
}