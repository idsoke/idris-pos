package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.ProductDto;
import com.warungku.pos.entity.StockMovement;
import com.warungku.pos.entity.enums.MovementType;
import com.warungku.pos.repository.StockMovementRepository;
import com.warungku.pos.service.StockService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Stock Management Controller
 */
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StockController {

    private final StockService stockService;
    private final StockMovementRepository stockMovementRepository;

    /**
     * Adjust stock for a product
     */
    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<ProductDto>> adjustStock(@Valid @RequestBody StockAdjustRequest request) {
        var product = stockService.adjustStock(request.getProductId(), request.getNewStock(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted", ProductDto.fromEntity(product)));
    }

    /**
     * Add stock (purchase/receive)
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductDto>> addStock(@Valid @RequestBody StockAddRequest request) {
        var product = stockService.addStock(
                request.getProductId(),
                request.getQuantity(),
                MovementType.PURCHASE,
                "PURCHASE",
                null,
                request.getNotes()
        );
        return ResponseEntity.ok(ApiResponse.success("Stock added", ProductDto.fromEntity(product)));
    }

    /**
     * Get stock movements for a product
     */
    @GetMapping("/movements/{productId}")
    public ResponseEntity<ApiResponse<Page<StockMovement>>> getMovements(
            @PathVariable Long productId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                stockMovementRepository.findByProductIdOrderByMovementDateDesc(productId, pageable)
        ));
    }

    /**
     * Get all stock movements
     */
    @GetMapping("/movements")
    public ResponseEntity<ApiResponse<List<StockMovement>>> getAllMovements(
            @RequestParam(required = false) MovementType type) {
        List<StockMovement> movements = type != null
                ? stockMovementRepository.findByMovementType(type)
                : stockMovementRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(movements));
    }

    @Data
    public static class StockAdjustRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "New stock is required")
        private Integer newStock;

        private String notes;
    }

    @Data
    public static class StockAddRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private String notes;
    }
}
