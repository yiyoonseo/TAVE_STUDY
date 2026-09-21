package com.example.nplus1.repository;

import com.example.nplus1.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // 순수 JPA에서 em.persist(product); 처럼 사용했던 것과 달리,
    // Spring Data JPA는 JpaRepository를 상속하면 기본 CRUD 구현을 Spring이 런타임에 만들어줌
    // JpaRepository<Product, Long>
    // Product -> 이 Repository가 관리할 엔티티
    // Long -> Product의 PK 타입
}
