package com.warungku.pos.service;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.entity.Product;
import com.warungku.pos.entity.StockMovement;
import com.warungku.pos.entity.User;
import com.warungku.pos.entity.enums.MovementType;
import com.warungku.pos.exception.BadRequestException;
import com.warungku.pos.exception.NotFoundException;
import com.warungku.pos.repository.ProductRepository;
import com.warungku.pos.repository.StockMovementRepository;
import com.warungku.pos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    @Transactional
    public Product reduceStock(Long productId, int quantity, String referenceType, Long referenceId, String notes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        if (product.getStock() < quantity) {
            throw new BadRequestException(
                    String.format("Insufficient stock for '%s'. Available: %d, Requested: %d",
                            product.getName(), product.getStock(), quantity));
        }

        int stockBefore = product.getStock();
        int stockAfter = stockBefore - quantity;

        product.setStock(stockAfter);
        product = productRepository.save(product);

        // Record movement
        recordMovement(product, MovementType.SALE, -quantity, stockBefore, stockAfter, referenceType, referenceId, notes);

        log.debug("Stock reduced for product {}: {} -> {}", product.getSku(), stockBefore, stockAfter);
        return product;
    }

    @Transactional
    public Product addStock(Long productId, int quantity, MovementType type, String referenceType, Long referenceId, String notes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        int stockBefore = product.getStock();
        int stockAfter = stockBefore + quantity;

        product.setStock(stockAfter);
        product = productRepository.save(product);

        recordMovement(product, type, quantity, stockBefore, stockAfter, referenceType, referenceId, notes);

        log.debug("Stock added for product {}: {} -> {}", product.getSku(), stockBefore, stockAfter);
        return product;
    }

    @Transactional
    public Product adjustStock(Long productId, int newStock, String notes) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        int stockBefore = product.getStock();
        int difference = newStock - stockBefore;

        product.setStock(newStock);
        product = productRepository.save(product);

        recordMovement(product, MovementType.ADJUSTMENT, difference, stockBefore, newStock, "ADJUSTMENT", null, notes);

        log.info("Stock adjusted for product {}: {} -> {}", product.getSku(), stockBefore, newStock);
        return product;
    }

    @Transactional
    public void restoreStock(Long productId, int quantity, String referenceType, Long referenceId, String notes) {
        addStock(productId, quantity, MovementType.RETURN, referenceType, referenceId, notes);
    }

    private void recordMovement(Product product, MovementType type, int quantity, int stockBefore, int stockAfter,
                                String referenceType, Long referenceId, String notes) {
        Long userId = TenantContext.getUserId();
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        StockMovement movement = StockMovement.builder()
                .product(product)
                .movementType(type)
                .quantity(Math.abs(quantity))
                .stockBefore(stockBefore)
                .stockAfter(stockAfter)
                .movementDate(LocalDateTime.now())
                .referenceType(referenceType)
                .referenceId(referenceId)
                .notes(notes)
                .createdBy(user)
                .tenantId(TenantContext.getTenantId())
                .build();

        stockMovementRepository.save(movement);
    }
}
