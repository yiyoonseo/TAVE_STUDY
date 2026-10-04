package com.example.dustbank.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ChildCreateRequest(
        @NotBlank(message = "nickname은 필수입니다")
        String nickname,

        @Min(value = 1, message = "iconId는 1 이상이어야 합니다")
        @Max(value = 4, message = "iconId는 4 이하여야 합니다")
        int iconId
) {
}
