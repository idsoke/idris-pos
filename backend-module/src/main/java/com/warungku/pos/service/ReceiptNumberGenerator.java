package com.warungku.pos.service;

import com.warungku.pos.core.tenant.TenantContext;
import com.warungku.pos.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class ReceiptNumberGenerator {

    private final SaleRepository saleRepository;
    private final AtomicLong counter = new AtomicLong(0);
    private String lastDate = "";

    public synchronized String generate() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long tenantId = TenantContext.getTenantId();
        String prefix = String.format("INV-%d-%s-", tenantId != null ? tenantId : 0, today);

        // Reset counter if date changed
        if (!today.equals(lastDate)) {
            lastDate = today;
            
            // Get last receipt number from database
            String lastReceipt = saleRepository.findLastReceiptNumberByPrefix(prefix);
            if (lastReceipt != null) {
                try {
                    String numPart = lastReceipt.substring(lastReceipt.lastIndexOf("-") + 1);
                    counter.set(Long.parseLong(numPart));
                } catch (Exception e) {
                    counter.set(0);
                }
            } else {
                counter.set(0);
            }
        }

        long nextNum = counter.incrementAndGet();
        return String.format("%s%04d", prefix, nextNum);
    }
}
