package com.example.dustbank.service;

import com.example.dustbank.domain.Child;
import com.example.dustbank.domain.Parent;
import com.example.dustbank.dto.ChildCreateRequest;
import com.example.dustbank.dto.ChildResponse;
import com.example.dustbank.exception.BusinessException;
import com.example.dustbank.exception.ErrorCode;
import com.example.dustbank.repository.ChildRepository;
import com.example.dustbank.repository.ParentRepository;
import com.example.dustbank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildService {

    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public ChildResponse create(String parentId, ChildCreateRequest request) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_NOT_FOUND));
        Child child = new Child(parent, request.nickname(), request.iconId());
        childRepository.save(child);
        return ChildResponse.from(child);
    }

    public List<ChildResponse> getChildren(String parentId) {
        if (!parentRepository.existsById(parentId)) {
            throw new BusinessException(ErrorCode.PARENT_NOT_FOUND);
        }
        return childRepository.findByParent_ParentIdOrderByChildIdAsc(parentId).stream()
                .map(ChildResponse::from)
                .toList();
    }

    public ChildResponse get(Long childId) {
        return ChildResponse.from(findChild(childId));
    }

    @Transactional
    @CacheEvict(value = "weeklyStats", allEntries = true)
    public void delete(Long childId) {
        Child child = findChild(childId);
        transactionRepository.deleteByChild_ChildId(childId);
        childRepository.delete(child);
    }



    private Child findChild(Long childId) {
        return childRepository.findById(childId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHILD_NOT_FOUND));
    }
}