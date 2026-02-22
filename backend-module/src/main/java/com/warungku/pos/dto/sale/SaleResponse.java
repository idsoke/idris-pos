package com.warungku.pos.dto.sale;

import com.warungku.pos.entity.Sale;
import com.warungku.pos.entity.enums.PaymentMethod;
import com.warungku.pos.entity.enums.PaymentStatus;
import com.warungku.pos.entity.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponse {

    private Long id;
    private String receiptNumber;
    private LocalDateTime saleDate;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal grandTotal;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private SaleStatus status;
    private String notes;
    private String customerName;
    private String customerPhone;
    private Long cashierId;
    private String cashierName;
    private int totalItems;
    private List<SaleItemResponse> items;

    public static SaleResponse fromEntity(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .receiptNumber(sale.getReceiptNumber())
                .saleDate(sale.getSaleDate())
                .subtotal(sale.getSubtotal())
                .taxRate(sale.getTaxRate())
                .taxAmount(sale.getTaxAmount())
                .discountAmount(sale.getDiscountAmount())
                .discountPercent(sale.getDiscountPercent())
                .grandTotal(sale.getGrandTotal())
                .paymentMethod(sale.getPaymentMethod())
                .paymentStatus(sale.getPaymentStatus())
                .amountPaid(sale.getAmountPaid())
                .changeAmount(sale.getChangeAmount())
                .status(sale.getStatus())
                .notes(sale.getNotes())
                .customerName(sale.getCustomerName())
                .customerPhone(sale.getCustomerPhone())
                .cashierId(sale.getCashier().getId())
                .cashierName(sale.getCashier().getName())
                .totalItems(sale.getTotalItems())
                .items(sale.getItems().stream().map(SaleItemResponse::fromEntity).toList())
                .build();
    }
}
