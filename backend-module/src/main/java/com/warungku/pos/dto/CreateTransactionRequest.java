package com.warungku.pos.dto;

import com.warungku.pos.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateTransactionRequest {
    
    @NotEmpty(message = "Transaction must have at least one item")
    private List<CartItem> items;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    private BigDecimal cashReceived;
    
    private BigDecimal discount;
    
    private String notes;
    
    @Data
    public static class CartItem {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
        
        private String notes;
    }
}
