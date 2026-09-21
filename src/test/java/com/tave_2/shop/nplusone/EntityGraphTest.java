package com.tave_2.shop.nplusone;

import com.tave_2.shop.domain.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityGraphTest extends NPlusOneTestSupport {

    @Test
    void 엔티티_그래프로_회원을_함께_조회하면_쿼리가_1번_나간다() {
        System.out.println("===== 주문 조회 (@EntityGraph: member) =====");
        List<Order> orders = orderRepository.findAllWithMemberEntityGraph();

        System.out.println("===== 회원 이름 접근 =====");
        for (Order order : orders) {
            System.out.println(order.getMember().getName());
        }

        assertThat(queryCount()).isEqualTo(1);
    }

    @Test
    void 엔티티_그래프로_주문상품을_함께_조회하면_쿼리가_1번_나간다() {
        System.out.println("===== 주문 조회 (@EntityGraph: orderItems) =====");
        List<Order> orders = orderRepository.findAllWithOrderItemsEntityGraph();

        System.out.println("===== 주문상품 접근 =====");
        for (Order order : orders) {
            System.out.println(order.getId() + "번 주문 상품 수: " + order.getOrderItems().size());
        }

        assertThat(queryCount()).isEqualTo(1);
        assertThat(orders).hasSize(ORDER_COUNT);
    }
}
