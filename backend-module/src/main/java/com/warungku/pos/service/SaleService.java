package com.warungku.pos.service;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.dto.sale.CreateSaleRequest;
import com.warungku.pos.dto.sale.SaleResponse;
import com.warungku.pos.entity.*;
import com.warungku.pos.entity.enums.PaymentMethod;
import com.warungku.pos.entity.enums.PaymentStatus;
import com.warungku.pos.entity.enums.SaleStatus;
import com.warungku.pos.exception.BadRequestException;
import com.warungku.pos.exception.NotFoundException;
import com.warungku.pos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OutletRepository outletRepository;
    private final ReceiptNumberGenerator receiptNumberGenerator;
    private final StockService stockService;

    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.10");

    public Page<SaleResponse> getSales(Pageable pageable) {
        return saleRepository.findAllByOrderBySaleDateDesc(pageable)
                .map(SaleResponse::fromEntity);
    }

    public SaleResponse getSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale", id));
        return SaleResponse.fromEntity(sale);
    }

    public SaleResponse getSaleByReceipt(String receiptNumber) {
        Sale sale = saleRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new NotFoundException("Sale not found: " + receiptNumber));
        return SaleResponse.fromEntity(sale);
    }

    public List<SaleResponse> getTodaySales() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        return saleRepository.findBySaleDateBetween(start, end).stream()
                .map(SaleResponse::fromEntity)
                .toList();
    }

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request) {
        Long userId = TenantContext.getUserId();
        Long tenantId = TenantContext.getTenantId();

        User cashier = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        Outlet outlet = outletRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Outlet", tenantId));

        BigDecimal taxRate = outlet.getTaxRate() != null ? outlet.getTaxRate() : DEFAULT_TAX_RATE;

        // Create sale
        Sale sale = Sale.builder()
                .receiptNumber(receiptNumberGenerator.generate())
                .saleDate(LocalDateTime.now())
                .taxRate(taxRate)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PAID)
                .status(SaleStatus.COMPLETED)
                .notes(request.getNotes())
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .cashier(cashier)
                .tenantId(tenantId)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        // Process items
        for (CreateSaleRequest.SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product", itemReq.getProductId()));

            // Validate stock
            if (product.getStock() < itemReq.getQuantity()) {
                throw new BadRequestException(
                        String.format("Insufficient stock for '%s'. Available: %d, Requested: %d",
                                product.getName(), product.getStock(), itemReq.getQuantity()));
            }

            BigDecimal itemDiscount = itemReq.getDiscountAmount() != null ? itemReq.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal itemSubtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()))
                    .subtract(itemDiscount);

            SaleItem saleItem = SaleItem.builder()
                    .product(product)
                    .productSku(product.getSku())
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(itemReq.getQuantity())
                    .discountAmount(itemDiscount)
                    .subtotal(itemSubtotal)
                    .notes(itemReq.getNotes())
                    .build();

            sale.addItem(saleItem);
            subtotal = subtotal.add(itemSubtotal);
        }

        // Calculate totals
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal discountPercent = request.getDiscountPercent() != null ? request.getDiscountPercent() : BigDecimal.ZERO;

        // Apply percentage discount if provided
        if (discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = discountAmount.add(
                    subtotal.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
            );
        }

        BigDecimal afterDiscount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = afterDiscount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grandTotal = afterDiscount.add(taxAmount);

        sale.setSubtotal(subtotal);
        sale.setDiscountAmount(discountAmount);
        sale.setDiscountPercent(discountPercent);
        sale.setTaxAmount(taxAmount);
        sale.setGrandTotal(grandTotal);

        // Handle payment
        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            if (request.getAmountPaid() == null || request.getAmountPaid().compareTo(grandTotal) < 0) {
                throw new BadRequestException("Amount paid must be >= grand total for cash payment");
            }
            sale.setAmountPaid(request.getAmountPaid());
            sale.setChangeAmount(request.getAmountPaid().subtract(grandTotal));
        } else {
            sale.setAmountPaid(grandTotal);
            sale.setChangeAmount(BigDecimal.ZERO);
        }

        // Save sale
        sale = saleRepository.save(sale);

        // Reduce stock for each item
        for (SaleItem item : sale.getItems()) {
            stockService.reduceStock(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    "SALE",
                    sale.getId(),
                    "Sale: " + sale.getReceiptNumber()
            );
        }

        log.info("Sale created: {} with {} items, total: {}",
                sale.getReceiptNumber(), sale.getTotalItems(), sale.getGrandTotal());

        return SaleResponse.fromEntity(sale);
    }

    @Transactional
    public SaleResponse voidSale(Long id, String reason) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale", id));

        if (sale.getStatus() != SaleStatus.COMPLETED) {
            throw new BadRequestException("Only completed sales can be voided");
        }

        // Restore stock
        for (SaleItem item : sale.getItems()) {
            stockService.restoreStock(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    "VOID",
                    sale.getId(),
                    "Void sale: " + sale.getReceiptNumber() + " - " + reason
            );
        }

        sale.setStatus(SaleStatus.VOIDED);
        sale.setPaymentStatus(PaymentStatus.REFUNDED);
        sale.setNotes((sale.getNotes() != null ? sale.getNotes() + " | " : "") + "VOIDED: " + reason);

        sale = saleRepository.save(sale);
        log.info("Sale voided: {} - Reason: {}", sale.getReceiptNumber(), reason);

        return SaleResponse.fromEntity(sale);
    }

    @Transactional
    public SaleResponse refundSale(Long id, String reason) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale", id));

        if (sale.getStatus() != SaleStatus.COMPLETED) {
            throw new BadRequestException("Only completed sales can be refunded");
        }

        // Restore stock
        for (SaleItem item : sale.getItems()) {
            stockService.restoreStock(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    "REFUND",
                    sale.getId(),
                    "Refund: " + sale.getReceiptNumber() + " - " + reason
            );
        }

        sale.setStatus(SaleStatus.REFUNDED);
        sale.setPaymentStatus(PaymentStatus.REFUNDED);
        sale.setNotes((sale.getNotes() != null ? sale.getNotes() + " | " : "") + "REFUNDED: " + reason);

        sale = saleRepository.save(sale);
        log.info("Sale refunded: {} - Reason: {}", sale.getReceiptNumber(), reason);

        return SaleResponse.fromEntity(sale);
    }
}
