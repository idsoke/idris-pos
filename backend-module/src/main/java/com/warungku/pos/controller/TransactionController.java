package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.dto.CreateTransactionRequest;
import com.warungku.pos.dto.TransactionDto;
import com.warungku.pos.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Transaction Controller.
 * Handles POS transactions - scoped by tenant.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> getTransactions(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactions(pageable)));
    }
    
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getTodayTransactions() {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTodayTransactions()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransaction(id)));
    }
    
    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransactionByInvoice(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionByInvoice(invoiceNumber)));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Transaction created", 
                transactionService.createTransaction(request)));
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TransactionDto>> cancelTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Transaction cancelled", 
                transactionService.cancelTransaction(id)));
    }
}
