package com.example.dustbank.dto;

import com.example.dustbank.domain.IncomeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record IncomeCreateRequest(
        @NotNull(message = "incomeType은 필수입니다")
        IncomeType incomeType,

        @Positive(message = "amount는 0보다 커야 합니다")
        long amount,

        @Size(max = 255, message = "memo는 255자 이하여야 합니다")
        String memo,

        LocalDateTime transactedAt
) {
}