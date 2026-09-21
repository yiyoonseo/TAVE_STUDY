package com.tave_2.shop.nplusone;

import com.tave_2.shop.domain.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NPlusOneTest extends NPlusOneTestSupport {

    @Test
    void 주문_목록에서_회원을_조회하면_N_플러스_1이_발생한다() {
        System.out.println("===== 주문 목록 조회 =====");
        List<Order> orders = orderRepository.findAll(); // 쿼리 1번

        System.out.println("===== 회원 이름 접근 =====");
        for (Order order : orders) {
            // member는 LAZY라서 프록시 상태 → getName() 시점에 SELECT 발생
            System.out.println(order.getMember().getName());
        }

        // 주문 조회 1번 + 회원 조회 N(5)번
        assertThat(queryCount()).isEqualTo(1 + ORDER_COUNT);
    }

    @Test
    void 주문_목록에서_주문상품을_조회하면_N_플러스_1이_발생한다() {
        System.out.println("===== 주문 목록 조회 =====");
        List<Order> orders = orderRepository.findAll(); // 쿼리 1번

        System.out.println("===== 주문상품 접근 =====");
        for (Order order : orders) {
            // 컬렉션도 기본이 LAZY → size() 시점에 주문마다 SELECT 발생
            System.out.println(order.getId() + "번 주문 상품 수: " + order.getOrderItems().size());
        }

        // 주문 조회 1번 + 주문상품 조회 N(5)번
        assertThat(queryCount()).isEqualTo(1 + ORDER_COUNT);
    }
}
