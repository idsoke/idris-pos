package com.warungku.pos.entity.subscription;

public enum SubscriptionStatus {
    TRIAL,          // Free trial period
    ACTIVE,         // Paid and active
    PAST_DUE,       // Payment failed, grace period
    CANCELLED,      // User cancelled
    EXPIRED,        // Subscription ended
    SUSPENDED       // Admin suspended
}
