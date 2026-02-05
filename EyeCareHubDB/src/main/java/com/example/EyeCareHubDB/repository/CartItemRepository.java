package com.example.EyeCareHubDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.EyeCareHubDB.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
