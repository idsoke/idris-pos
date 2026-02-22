package com.warungku.pos.entity;

import com.warungku.pos.entity.enums.TableStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "restaurant_tables", indexes = {
        @Index(name = "idx_table_tenant", columnList = "tenant_id")
})
public class RestaurantTable extends TenantBaseEntity {

    @Column(nullable = false)
    private String name;

    private Integer capacity;

    private String location; // e.g. "Indoor", "Terrace"

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false) // columnDefinition is often DB specific, length is safer
    private TableStatus status = TableStatus.AVAILABLE;
}
