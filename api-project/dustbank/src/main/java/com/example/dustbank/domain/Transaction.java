package com.example.dustbank.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private NeedType needType;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory expenseCategory;

    @Enumerated(EnumType.STRING)
    private IncomeType incomeType;

    private long amount;

    private String memo;

    private LocalDateTime transactedAt;

    private LocalDateTime createdAt;

    private Transaction(Child child, TransactionType type, NeedType needType,
                        ExpenseCategory expenseCategory, IncomeType incomeType,
                        long amount, String memo, LocalDateTime transactedAt) {
        this.child = child;
        this.type = type;
        this.needType = needType;
        this.expenseCategory = expenseCategory;
        this.incomeType = incomeType;
        this.amount = amount;
        this.memo = memo;
        this.transactedAt = transactedAt;
        this.createdAt = LocalDateTime.now();
    }

    // 수입 생성 메서드
    public static Transaction income(Child child, IncomeType incomeType, long amount,
                                     String memo, LocalDateTime transactedAt) {
        return new Transaction(child, TransactionType.INCOME, null, null,
                incomeType, amount, memo, transactedAt);
    }

    // 지출 생성 메서드
    public static Transaction expense(Child child, NeedType needType, ExpenseCategory expenseCategory,
                                      long amount, String memo, LocalDateTime transactedAt) {
        return new Transaction(child, TransactionType.EXPENSE, needType, expenseCategory,
                null, amount, memo, transactedAt);
    }

    public void updateIncome(IncomeType incomeType, long amount, String memo, LocalDateTime transactedAt) {
        this.incomeType = incomeType;
        this.amount = amount;
        this.memo = memo;
        this.transactedAt = transactedAt;
    }

    public void updateExpense(NeedType needType, ExpenseCategory expenseCategory,
                              long amount, String memo, LocalDateTime transactedAt) {
        this.needType = needType;
        this.expenseCategory = expenseCategory;
        this.amount = amount;
        this.memo = memo;
        this.transactedAt = transactedAt;
    }
}