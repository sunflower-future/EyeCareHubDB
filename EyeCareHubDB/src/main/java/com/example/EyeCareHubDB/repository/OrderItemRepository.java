package com.example.EyeCareHubDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.EyeCareHubDB.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
