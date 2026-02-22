package com.warungku.pos.repository;

import com.warungku.pos.entity.StockMovement;
import com.warungku.pos.entity.enums.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId);

    Page<StockMovement> findByProductIdOrderByMovementDateDesc(Long productId, Pageable pageable);

    List<StockMovement> findByMovementType(MovementType movementType);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementDate BETWEEN :start AND :end ORDER BY sm.movementDate DESC")
    List<StockMovement> findByDateRange(LocalDateTime start, LocalDateTime end);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.referenceType = :refType AND sm.referenceId = :refId")
    List<StockMovement> findByReference(String refType, Long refId);
}
