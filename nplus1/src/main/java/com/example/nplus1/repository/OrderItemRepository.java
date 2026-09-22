package com.example.nplus1.repository;

import com.example.nplus1.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 1. fetch join
    @Query("""
        select oi
        from OrderItem oi
        join fetch oi.product
    """)
    List<OrderItem> findAllWithProduct();
}
