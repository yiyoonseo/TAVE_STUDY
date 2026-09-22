package com.example.nplus1.repository;

import com.example.nplus1.domain.OrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
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

    // 2. @EntityGraph
    @EntityGraph(attributePaths = "product")
    @Query("""
        select oi
        from OrderItem oi
    """) // 만약 기본 findAll()을 오버라이딩 한다면 @Query를 작성할 필요가 없으나, findAll()은 현재 N+1 발생 확인용으로 사용 중이므로 새 메서드를 만듦
    List<OrderItem> findAllWithEntityGraph();
}
