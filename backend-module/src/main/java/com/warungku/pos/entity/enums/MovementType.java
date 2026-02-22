package com.warungku.pos.entity.enums;

public enum MovementType {
    SALE,           // Stock out from sale
    PURCHASE,       // Stock in from purchase
    ADJUSTMENT,     // Manual adjustment
    RETURN,         // Customer return
    TRANSFER_IN,    // Transfer from another outlet
    TRANSFER_OUT,   // Transfer to another outlet
    DAMAGE,         // Damaged goods
    EXPIRED,        // Expired goods
    INITIAL         // Initial stock
}
