package com.example.dustbank.dto;

import com.example.dustbank.domain.ExpenseCategory;
import com.example.dustbank.domain.NeedType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ExpenseCreateRequest(
        @NotNull(message = "needType은 필수입니다")
        NeedType needType,

        @NotNull(message = "expenseCategory는 필수입니다")
        ExpenseCategory expenseCategory,

        @Positive(message = "amount는 0보다 커야 합니다")
        long amount,

        @Size(max = 255, message = "memo는 255자 이하여야 합니다")
        String memo,

        LocalDateTime transactedAt
) {
}