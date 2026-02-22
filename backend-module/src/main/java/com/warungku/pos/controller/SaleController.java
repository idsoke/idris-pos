package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.sale.CreateSaleRequest;
import com.warungku.pos.dto.sale.SaleResponse;
import com.warungku.pos.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Sales Controller - POS Transaction Endpoints
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    /**
     * Get all sales with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SaleResponse>>> getSales(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(saleService.getSales(pageable)));
    }

    /**
     * Get today's sales
     */
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getTodaySales() {
        return ResponseEntity.ok(ApiResponse.success(saleService.getTodaySales()));
    }

    /**
     * Get sale by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponse>> getSale(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(saleService.getSale(id)));
    }

    /**
     * Get sale by receipt number
     */
    @GetMapping("/receipt/{receiptNumber}")
    public ResponseEntity<ApiResponse<SaleResponse>> getSaleByReceipt(@PathVariable String receiptNumber) {
        return ResponseEntity.ok(ApiResponse.success(saleService.getSaleByReceipt(receiptNumber)));
    }

    /**
     * Create new sale (checkout)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(@Valid @RequestBody CreateSaleRequest request) {
        SaleResponse response = saleService.createSale(request);
        return ResponseEntity.ok(ApiResponse.success("Sale completed successfully", response));
    }

    /**
     * Void sale (Admin only)
     */
    @PostMapping("/{id}/void")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaleResponse>> voidSale(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(ApiResponse.success("Sale voided", saleService.voidSale(id, reason)));
    }

    /**
     * Refund sale (Admin only)
     */
    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SaleResponse>> refundSale(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(ApiResponse.success("Sale refunded", saleService.refundSale(id, reason)));
    }
}
