package com.example.dustbank.controller;

import com.example.dustbank.dto.WeeklyStatsResponse;
import com.example.dustbank.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/*
통계(Stats) 리소스를 다루는 컨트롤러
통계는 "어느 자녀의 통계인가"가 필요하므로 자녀 밑에 둠 (/children/{childId}/stats/...)
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/children/{childId}/stats/weekly")
    public WeeklyStatsResponse getWeekly(
            @PathVariable Long childId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return statsService.getWeekly(childId, date);
    }
}
