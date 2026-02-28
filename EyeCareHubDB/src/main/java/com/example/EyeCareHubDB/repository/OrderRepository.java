package com.example.EyeCareHubDB.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.EyeCareHubDB.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
        Page<Order> findByAccountId(Long accountId, Pageable pageable);

        Page<Order> findByAccountIdAndStatus(Long accountId, Order.OrderStatus status, Pageable pageable);

        Page<Order> findAll(Pageable pageable);

        Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

        @Query("SELECT o FROM Order o WHERE " +
                        "(:query IS NULL OR o.orderNumber LIKE %:query% OR " +
                        "o.account.email LIKE %:query% OR " +
                        "o.phoneNumber LIKE %:query% OR " +
                        "o.account.customer.firstName LIKE %:query% OR " +
                        "o.account.customer.lastName LIKE %:query%) AND " +
                        "(:status IS NULL OR o.status = :status) AND " +
                        "(:orderType IS NULL OR o.orderType = :orderType) AND " +
                        "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)")
        Page<Order> searchOrders(@Param("query") String query,
                        @Param("status") Order.OrderStatus status,
                        @Param("orderType") Order.OrderType orderType,
                        @Param("paymentStatus") Order.PaymentStatus paymentStatus,
                        Pageable pageable);

        @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
        List<Object[]> countOrdersByStatus();

        boolean existsByOrderNumber(String orderNumber);
}
