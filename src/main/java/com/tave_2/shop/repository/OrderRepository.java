package com.tave_2.shop.repository;

import com.tave_2.shop.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 해결 1: 페치 조인
    @Query("select o from Order o join fetch o.member")
    List<Order> findAllWithMemberFetchJoin();

    // Hibernate 6부터는 컬렉션 페치 조인 결과의 중복을 자동으로 제거해서 distinct가 필요 없음
    @Query("select o from Order o join fetch o.orderItems")
    List<Order> findAllWithOrderItemsFetchJoin();

    // 해결 2: @EntityGraph (JPQL에 join fetch를 직접 쓰지 않고 어노테이션으로 지정)
    @EntityGraph(attributePaths = "member")
    @Query("select o from Order o")
    List<Order> findAllWithMemberEntityGraph();

    @EntityGraph(attributePaths = "orderItems")
    @Query("select o from Order o")
    List<Order> findAllWithOrderItemsEntityGraph();

    // 해결 3: 배치 사이즈는 쿼리를 바꾸지 않고 설정으로 해결 → 기본 findAll() 그대로 사용
}
