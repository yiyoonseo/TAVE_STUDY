package com.example.dustbank.service;

import com.example.dustbank.domain.Parent;
import com.example.dustbank.dto.ParentCreateRequest;
import com.example.dustbank.dto.ParentResponse;
import com.example.dustbank.exception.BusinessException;
import com.example.dustbank.exception.ErrorCode;
import com.example.dustbank.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {

    private final ParentRepository parentRepository;

    @Transactional
    public ParentResponse create(ParentCreateRequest request) {
        if (parentRepository.existsById(request.parentId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PARENT_ID);
        }
        Parent parent = new Parent(request.parentId(), request.nickname(), request.iconId());
        parentRepository.save(parent);
        return ParentResponse.from(parent);
    }

    public ParentResponse get(String parentId) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_NOT_FOUND));
        return ParentResponse.from(parent);
    }
}
