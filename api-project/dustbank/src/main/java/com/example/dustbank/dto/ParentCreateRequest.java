package com.example.dustbank.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ParentCreateRequest(
        @NotBlank(message = "parentId는 필수입니다")
        String parentId,

        @NotBlank(message = "nickname은 필수입니다")
        String nickname,

        @Min(value = 5, message = "iconId는 5 이상이어야 합니다")
        @Max(value = 8, message = "iconId는 8 이하여야 합니다") // 현재 부모 아이콘은 5만 존재하나 확장성 고려
        int iconId
) {
}