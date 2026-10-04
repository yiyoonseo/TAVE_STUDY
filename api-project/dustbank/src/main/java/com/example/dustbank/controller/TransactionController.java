package com.example.dustbank.controller;

import com.example.dustbank.domain.TransactionType;
import com.example.dustbank.dto.ExpenseCreateRequest;
import com.example.dustbank.dto.IncomeCreateRequest;
import com.example.dustbank.dto.TransactionResponse;
import com.example.dustbank.dto.TransactionUpdateRequest;
import com.example.dustbank.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/*
거래(Transaction) 리소스를 다루는 컨트롤러
수입/지출 등록은 /children/{childId} 밑, 단건 조회/수정/삭제는 /transactions/{transactionId}로 접근
시작 경로가 두 가지라 공통 경로는 /api/v1까지만 두고 메서드마다 나머지를 적음
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/children/{childId}/incomes")
    public ResponseEntity<TransactionResponse> createIncome(@PathVariable Long childId,
                                                            @Valid @RequestBody IncomeCreateRequest request) {
        TransactionResponse response = transactionService.createIncome(childId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/children/{childId}/expenses")
    public ResponseEntity<TransactionResponse> createExpense(@PathVariable Long childId,
                                                             @Valid @RequestBody ExpenseCreateRequest request) {
        TransactionResponse response = transactionService.createExpense(childId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/children/{childId}/transactions")
    public List<TransactionResponse> getTransactions(
            @PathVariable Long childId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return transactionService.getTransactions(childId, type, from, to);
    }

    @GetMapping("/transactions/{transactionId}")
    public TransactionResponse get(@PathVariable Long transactionId) {
        return transactionService.get(transactionId);
    }

    @PutMapping("/transactions/{transactionId}")
    public TransactionResponse update(@PathVariable Long transactionId,
                                      @Valid @RequestBody TransactionUpdateRequest request) {
        return transactionService.update(transactionId, request);
    }

    @DeleteMapping("/transactions/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
        transactionService.delete(transactionId);
        return ResponseEntity.noContent().build();
    }
}