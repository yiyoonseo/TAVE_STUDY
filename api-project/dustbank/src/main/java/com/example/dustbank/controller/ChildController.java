package com.example.dustbank.controller;

import com.example.dustbank.dto.ChildCreateRequest;
import com.example.dustbank.dto.ChildResponse;
import com.example.dustbank.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
자녀(Child) 리소스를 다루는 컨트롤러
컨트롤러는 URL의 첫 경로가 아니라 "다루는 리소스" 기준으로 나눔
따라서 /parents/{parentId}/children (목록, 등록)도 응답이 자녀이므로 이 컨트롤러에서 처리

<소유 규칙>
목록, 생성: 소유자(부모) 밑   → /parents/{parentId}/children
단건 조회, 삭제: 자기 ID로 접근 → /children/{childId}
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @GetMapping("/parents/{parentId}/children")
    public List<ChildResponse> getChildren(@PathVariable String parentId) {
        return childService.getChildren(parentId);
    }

    @PostMapping("/parents/{parentId}/children")
    public ResponseEntity<ChildResponse> create(@PathVariable String parentId,
                                                @Valid @RequestBody ChildCreateRequest request) {
        ChildResponse response = childService.create(parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/children/{childId}")
    public ChildResponse get(@PathVariable Long childId) {
        return childService.get(childId);
    }

    @DeleteMapping("/children/{childId}")
    public ResponseEntity<Void> delete(@PathVariable Long childId) {
        childService.delete(childId);
        return ResponseEntity.noContent().build();
    }
}
