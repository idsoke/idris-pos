package com.warungku.pos.dto.sale;

import com.warungku.pos.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateSaleRequest {

    @NotEmpty(message = "Sale must have at least one item")
    private List<SaleItemRequest> items;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    private BigDecimal amountPaid;

    private BigDecimal discountAmount;

    private BigDecimal discountPercent;

    private String customerName;

    private String customerPhone;

    private String notes;

    @Data
    public static class SaleItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        private String barcode;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;

        private BigDecimal discountAmount;

        private String notes;
    }
}
