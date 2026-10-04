package com.example.dustbank.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_TRANSACTION_FIELD(HttpStatus.BAD_REQUEST, "거래 유형에 맞는 분류 값을 입력해야 합니다."),
    PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부모를 찾을 수 없습니다."),
    CHILD_NOT_FOUND(HttpStatus.NOT_FOUND, "자녀를 찾을 수 없습니다."),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    DUPLICATE_PARENT_ID(HttpStatus.CONFLICT, "이미 존재하는 부모 ID입니다.");

    private final HttpStatus status;
    private final String message;
}
