package com.warungku.pos.service;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.dto.CreateTransactionRequest;
import com.warungku.pos.dto.TransactionDto;
import com.warungku.pos.entity.*;
import com.warungku.pos.entity.enums.PaymentMethod;
import com.warungku.pos.entity.enums.TransactionStatus;
import com.warungku.pos.exception.BadRequestException;
import com.warungku.pos.exception.NotFoundException;
import com.warungku.pos.repository.OutletRepository;
import com.warungku.pos.repository.ProductRepository;
import com.warungku.pos.repository.TransactionRepository;
import com.warungku.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OutletRepository outletRepository;
    
    private static final AtomicLong invoiceCounter = new AtomicLong(System.currentTimeMillis() % 10000);
    
    public Page<TransactionDto> getTransactions(Pageable pageable) {
        return transactionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(TransactionDto::fromEntity);
    }
    
    public TransactionDto getTransaction(Long id) {
        Transaction trx = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction", id));
        return TransactionDto.fromEntity(trx);
    }
    
    public TransactionDto getTransactionByInvoice(String invoiceNumber) {
        Transaction trx = transactionRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new NotFoundException("Transaction not found: " + invoiceNumber));
        return TransactionDto.fromEntity(trx);
    }
    
    public List<TransactionDto> getTodayTransactions() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        
        return transactionRepository.findByDateRange(startOfDay, endOfDay).stream()
                .map(TransactionDto::fromEntity)
                .toList();
    }
    
    @Transactional
    public TransactionDto createTransaction(CreateTransactionRequest request) {
        Long userId = TenantContext.getUserId();
        Long tenantId = TenantContext.getTenantId();
        
        User cashier = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        
        Outlet outlet = outletRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Outlet", tenantId));
        
        Transaction transaction = Transaction.builder()
                .invoiceNumber(generateInvoiceNumber())
                .paymentMethod(request.getPaymentMethod())
                .discount(request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO)
                .notes(request.getNotes())
                .cashier(cashier)
                .status(TransactionStatus.COMPLETED)
                .build();
        transaction.setTenantId(tenantId);
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (CreateTransactionRequest.CartItem cartItem : request.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product", cartItem.getProductId()));
            
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for " + product.getName() + 
                        ". Available: " + product.getStock());
            }
            
            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            
            TransactionItem item = TransactionItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(itemSubtotal)
                    .notes(cartItem.getNotes())
                    .build();
            
            transaction.addItem(item);
            subtotal = subtotal.add(itemSubtotal);
            
            // Reduce stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        
        BigDecimal tax = subtotal.multiply(outlet.getTaxRate());
        BigDecimal total = subtotal.add(tax).subtract(transaction.getDiscount());
        
        transaction.setSubtotal(subtotal);
        transaction.setTax(tax);
        transaction.setTotal(total);
        
        // Handle cash payment
        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            if (request.getCashReceived() == null || request.getCashReceived().compareTo(total) < 0) {
                throw new BadRequestException("Cash received must be >= total amount");
            }
            transaction.setCashReceived(request.getCashReceived());
            transaction.setCashChange(request.getCashReceived().subtract(total));
        }
        
        transaction = transactionRepository.save(transaction);
        log.info("Transaction created: {} for tenant: {}", transaction.getInvoiceNumber(), tenantId);
        
        return TransactionDto.fromEntity(transaction);
    }
    
    @Transactional
    public TransactionDto cancelTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction", id));
        
        if (transaction.getStatus() != TransactionStatus.COMPLETED) {
            throw new BadRequestException("Only completed transactions can be cancelled");
        }
        
        // Restore stock
        for (TransactionItem item : transaction.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        
        transaction.setStatus(TransactionStatus.CANCELLED);
        transaction = transactionRepository.save(transaction);
        
        log.info("Transaction cancelled: {}", transaction.getInvoiceNumber());
        return TransactionDto.fromEntity(transaction);
    }
    
    private String generateInvoiceNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long counter = invoiceCounter.incrementAndGet();
        return String.format("INV-%s-%04d", date, counter % 10000);
    }
}
