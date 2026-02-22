package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.report.DailySalesReport;
import com.warungku.pos.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Sales Report Controller
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SalesReportController {

    private final ReportService reportService;

    /**
     * Get daily sales report
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailySalesReport>> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate reportDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(reportService.getDailySalesReport(reportDate)));
    }

    /**
     * Get weekly sales report (7 days from start date)
     */
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<List<DailySalesReport>>> getWeeklyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(6);
        return ResponseEntity.ok(ApiResponse.success(reportService.getWeeklySalesReport(start)));
    }

    /**
     * Get today's quick summary
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<DailySalesReport>> getTodayReport() {
        return ResponseEntity.ok(ApiResponse.success(reportService.getDailySalesReport(LocalDate.now())));
    }
}
