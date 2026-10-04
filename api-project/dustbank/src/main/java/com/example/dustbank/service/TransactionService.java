package com.example.dustbank.service;

import com.example.dustbank.domain.Child;
import com.example.dustbank.domain.Transaction;
import com.example.dustbank.domain.TransactionType;
import com.example.dustbank.dto.ExpenseCreateRequest;
import com.example.dustbank.dto.IncomeCreateRequest;
import com.example.dustbank.dto.TransactionResponse;
import com.example.dustbank.dto.TransactionUpdateRequest;
import com.example.dustbank.exception.BusinessException;
import com.example.dustbank.exception.ErrorCode;
import com.example.dustbank.repository.ChildRepository;
import com.example.dustbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ChildRepository childRepository;

    @Transactional
    @CacheEvict(value = "weeklyStats", allEntries = true)
    // 통계 키가 "자녀ID::기준날짜"라 영향받는 키를 특정하기 어려워 weeklyStats 전체를 삭제
    // (다른 자녀의 캐시도 지워지지만 다음 조회 때 한 번 다시 계산하면 되므로 허용)
    public TransactionResponse createIncome(Long childId, IncomeCreateRequest request) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));

        LocalDateTime transactedAt = request.transactedAt() != null
                ? request.transactedAt()
                : LocalDateTime.now();

        Transaction transaction = Transaction.income(
                child, request.incomeType(), request.amount(), request.memo(), transactedAt);
        transactionRepository.save(transaction);

        child.increaseBalance(request.amount());

        return TransactionResponse.from(transaction);
    }

    @Transactional
    @CacheEvict(value = "weeklyStats", allEntries = true)
    public TransactionResponse createExpense(Long childId, ExpenseCreateRequest request) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));

        LocalDateTime transactedAt = request.transactedAt() != null
                ? request.transactedAt()
                : LocalDateTime.now();

        child.decreaseBalance(request.amount()); // 잔액 차감을 먼저 하고 거래를 저장 (잔액이 부족하면 decreaseBalance에서 예외)

        Transaction transaction = Transaction.expense(
                child, request.needType(), request.expenseCategory(),
                request.amount(), request.memo(), transactedAt);
        transactionRepository.save(transaction);

        return TransactionResponse.from(transaction);
    }

    public List<TransactionResponse> getTransactions(Long childId, TransactionType type,
                                                     LocalDate from, LocalDate to) {
        if (!childRepository.existsById(childId)) {
            throw new BusinessException(ErrorCode.CHILD_NOT_FOUND);
        }

        LocalDateTime fromAt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toAt = to != null ? to.plusDays(1).atStartOfDay() : null;

        return transactionRepository.search(childId, type, fromAt, toAt).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    public TransactionResponse get(Long transactionId) {
        return TransactionResponse.from(findTransaction(transactionId));
    }

    @Transactional
    @CacheEvict(value = "weeklyStats", allEntries = true)
    public TransactionResponse update(Long transactionId, TransactionUpdateRequest request) {
        Transaction transaction = findTransaction(transactionId);
        Child child = transaction.getChild();
        long previousAmount = transaction.getAmount();
        LocalDateTime transactedAt = request.transactedAt() != null
                ? request.transactedAt()
                : transaction.getTransactedAt();

        if (transaction.getType() == TransactionType.INCOME) {
            if (request.incomeType() == null
                    || request.needType() != null || request.expenseCategory() != null) {
                throw new BusinessException(ErrorCode.INVALID_TRANSACTION_FIELD);
            }
            child.adjustBalance(request.amount() - previousAmount);
            transaction.updateIncome(request.incomeType(), request.amount(), request.memo(), transactedAt);
        } else {
            if (request.needType() == null || request.expenseCategory() == null
                    || request.incomeType() != null) {
                throw new BusinessException(ErrorCode.INVALID_TRANSACTION_FIELD);
            }
            child.adjustBalance(previousAmount - request.amount());
            transaction.updateExpense(request.needType(), request.expenseCategory(),
                    request.amount(), request.memo(), transactedAt);
        }

        return TransactionResponse.from(transaction);
    }

    @Transactional
    @CacheEvict(value = "weeklyStats", allEntries = true)
    public void delete(Long transactionId) {
        Transaction transaction = findTransaction(transactionId);
        Child child = transaction.getChild();

        long delta = transaction.getType() == TransactionType.INCOME
                ? -transaction.getAmount()
                : transaction.getAmount();
        child.adjustBalance(delta);

        transactionRepository.delete(transaction);
    }

    private Transaction findTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));
    }
}
