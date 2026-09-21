package com.tave_2.shop.nplusone;

import com.tave_2.shop.domain.Item;
import com.tave_2.shop.domain.Member;
import com.tave_2.shop.domain.Order;
import com.tave_2.shop.domain.OrderItem;
import com.tave_2.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * N+1 재현/해결 테스트 공통 데이터: 회원 5명이 각각 주문 1건씩 (주문마다 상품 2개)
 */
@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Transactional
abstract class NPlusOneTestSupport {

    static final int ORDER_COUNT = 5;

    @Autowired EntityManager em;
    @Autowired OrderRepository orderRepository;

    Statistics statistics;

    @BeforeEach
    void setUp() {
        Item book = new Item("JPA 책", 30000, 100);
        Item pen = new Item("볼펜", 1000, 500);
        em.persist(book);
        em.persist(pen);

        for (int i = 1; i <= ORDER_COUNT; i++) {
            Member member = new Member("회원" + i);
            em.persist(member);

            Order order = new Order(member);
            order.addOrderItem(new OrderItem(book, 1));
            order.addOrderItem(new OrderItem(pen, 2));
            em.persist(order); // cascade로 OrderItem도 함께 저장
        }

        // 1차 캐시를 비워야 이후 조회가 실제 DB 쿼리로 나감
        em.flush();
        em.clear();

        statistics = em.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        statistics.clear();
    }

    long queryCount() {
        return statistics.getPrepareStatementCount();
    }
}
