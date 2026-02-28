package com.example.EyeCareHubDB.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.AfterSalesCase;

@Repository
public interface AfterSalesCaseRepository extends JpaRepository<AfterSalesCase, Long> {

    List<AfterSalesCase> findByAccountId(Long accountId);

    List<AfterSalesCase> findByOrderId(Long orderId);

    Page<AfterSalesCase> findAll(Pageable pageable);

    Page<AfterSalesCase> findByStatus(AfterSalesCase.CaseStatus status, Pageable pageable);

    Page<AfterSalesCase> findByAccountId(Long accountId, Pageable pageable);

    @Query("SELECT c FROM AfterSalesCase c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:query IS NULL OR c.reason LIKE %:query% OR c.description LIKE %:query% " +
            "OR c.account.email LIKE %:query% OR c.order.orderNumber LIKE %:query%)")
    Page<AfterSalesCase> searchCases(@Param("query") String query,
            @Param("status") AfterSalesCase.CaseStatus status,
            @Param("type") AfterSalesCase.CaseType type,
            Pageable pageable);
}
