package com.example.dustbank.dto;

import com.example.dustbank.domain.Parent;

public record ParentResponse(
        String parentId,
        String nickname,
        int iconId
) {
    public static ParentResponse from(Parent parent) {
        return new ParentResponse(
                parent.getParentId(),
                parent.getNickname(),
                parent.getIconId()
        );
    }
}