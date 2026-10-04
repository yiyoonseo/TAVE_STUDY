package com.example.dustbank.service;

import com.example.dustbank.domain.ExpenseCategory;
import com.example.dustbank.domain.NeedType;
import com.example.dustbank.domain.Transaction;
import com.example.dustbank.domain.TransactionType;
import com.example.dustbank.dto.WeeklyStatsResponse;
import com.example.dustbank.dto.WeeklyStatsResponse.CategoryAmount;
import com.example.dustbank.dto.WeeklyStatsResponse.NeedWantCount;
import com.example.dustbank.exception.BusinessException;
import com.example.dustbank.exception.ErrorCode;
import com.example.dustbank.repository.ChildRepository;
import com.example.dustbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final ChildRepository childRepository;
    private final TransactionRepository transactionRepository;

    @Cacheable(value = "weeklyStats",
            key = "#childId + '::' + (#date != null ? #date : T(java.time.LocalDate).now())") // 날짜를 보냈으면 그 날짜, 생략했으면 오늘 날짜
    public WeeklyStatsResponse getWeekly(Long childId, LocalDate date) {
        if (!childRepository.existsById(childId)) {
            throw new BusinessException(ErrorCode.CHILD_NOT_FOUND);
        }

        // 기준 날짜가 속한 주의 월요일 ~ 일요일
        LocalDate baseDate = date != null ? date : LocalDate.now();
        LocalDate weekStart = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        // 월요일 00:00 이상 ~ 다음 월요일 00:00 미만
        List<Transaction> transactions = transactionRepository.search(
                childId, null, weekStart.atStartOfDay(), weekEnd.plusDays(1).atStartOfDay());

        long totalIncome = 0;
        long totalExpense = 0;
        int needCount = 0;
        int wantCount = 0;

        // 거래가 없는 카테고리도 0으로 포함하기 위해 미리 0으로 채움 (EnumMap은 enum 선언 순서로 정렬됨)
        Map<ExpenseCategory, Long> categoryTotals = new EnumMap<>(ExpenseCategory.class);
        for (ExpenseCategory category : ExpenseCategory.values()) {
            categoryTotals.put(category, 0L);
        }

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
                if (transaction.getNeedType() == NeedType.NEED) {
                    needCount++;
                } else {
                    wantCount++;
                }
                categoryTotals.merge(transaction.getExpenseCategory(), transaction.getAmount(), Long::sum); // 키가 있으면 기존 값에 더하고, 없으면 새로 넣음
            }
        }

        List<CategoryAmount> categoryAmounts = categoryTotals.entrySet().stream()
                .map(entry -> new CategoryAmount(entry.getKey(), entry.getValue()))
                .toList();

        return new WeeklyStatsResponse(weekStart, weekEnd, totalIncome, totalExpense,
                new NeedWantCount(needCount, wantCount), categoryAmounts);
    }
}