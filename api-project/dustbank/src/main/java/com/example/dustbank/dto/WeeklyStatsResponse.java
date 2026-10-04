package com.example.dustbank.dto;

import com.example.dustbank.domain.ExpenseCategory;

import java.time.LocalDate;
import java.util.List;

public record WeeklyStatsResponse(
        LocalDate weekStart,
        LocalDate weekEnd,
        long totalIncome,
        long totalExpense,
        NeedWantCount needWantCount,
        List<CategoryAmount> categoryAmounts
) {
    public record NeedWantCount(int need, int want) {
    }

    public record CategoryAmount(ExpenseCategory category, long amount) {
    }
}