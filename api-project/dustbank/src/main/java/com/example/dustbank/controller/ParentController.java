package com.example.dustbank.controller;

import com.example.dustbank.dto.ParentCreateRequest;
import com.example.dustbank.dto.ParentResponse;
import com.example.dustbank.service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @PostMapping
    public ResponseEntity<ParentResponse> create(@Valid @RequestBody ParentCreateRequest request) {
        ParentResponse response = parentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{parentId}")
    public ParentResponse get(@PathVariable String parentId) {
        return parentService.get(parentId);
    }
}
