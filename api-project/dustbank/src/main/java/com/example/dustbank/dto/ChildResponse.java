package com.example.dustbank.dto;

import com.example.dustbank.domain.Child;

public record ChildResponse(
        Long childId,
        String parentId,
        String nickname,
        int iconId,
        long balance
) {
    public static ChildResponse from(Child child) {
        return new ChildResponse(
                child.getChildId(),
                child.getParent().getParentId(),
                child.getNickname(),
                child.getIconId(),
                child.getBalance()
        );
    }
}
