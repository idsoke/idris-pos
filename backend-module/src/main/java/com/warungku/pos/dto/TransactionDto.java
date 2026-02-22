package com.warungku.pos.dto;

import com.warungku.pos.entity.Transaction;
import com.warungku.pos.entity.enums.PaymentMethod;
import com.warungku.pos.entity.enums.TransactionStatus;
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
public class TransactionDto {
    private Long id;
    private String invoiceNumber;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private PaymentMethod paymentMethod;
    private BigDecimal cashReceived;
    private BigDecimal cashChange;
    private TransactionStatus status;
    private String notes;
    private Long cashierId;
    private String cashierName;
    private List<TransactionItemDto> items;
    private LocalDateTime createdAt;
    
    public static TransactionDto fromEntity(Transaction trx) {
        return TransactionDto.builder()
                .id(trx.getId())
                .invoiceNumber(trx.getInvoiceNumber())
                .subtotal(trx.getSubtotal())
                .tax(trx.getTax())
                .discount(trx.getDiscount())
                .total(trx.getTotal())
                .paymentMethod(trx.getPaymentMethod())
                .cashReceived(trx.getCashReceived())
                .cashChange(trx.getCashChange())
                .status(trx.getStatus())
                .notes(trx.getNotes())
                .cashierId(trx.getCashier().getId())
                .cashierName(trx.getCashier().getName())
                .items(trx.getItems().stream().map(TransactionItemDto::fromEntity).toList())
                .createdAt(trx.getCreatedAt())
                .build();
    }
}
