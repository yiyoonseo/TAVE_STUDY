package com.example.dustbank.dto;

import com.example.dustbank.domain.ExpenseCategory;
import com.example.dustbank.domain.IncomeType;
import com.example.dustbank.domain.NeedType;
import com.example.dustbank.domain.Transaction;
import com.example.dustbank.domain.TransactionType;

import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        Long childId,
        TransactionType type,
        NeedType needType,
        ExpenseCategory expenseCategory,
        IncomeType incomeType,
        long amount,
        String memo,
        LocalDateTime transactedAt,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getChild().getChildId(),
                transaction.getType(),
                transaction.getNeedType(),
                transaction.getExpenseCategory(),
                transaction.getIncomeType(),
                transaction.getAmount(),
                transaction.getMemo(),
                transaction.getTransactedAt(),
                transaction.getCreatedAt()
        );
    }
}