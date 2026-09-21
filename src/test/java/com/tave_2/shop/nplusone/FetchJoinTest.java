package com.tave_2.shop.nplusone;

import com.tave_2.shop.domain.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FetchJoinTest extends NPlusOneTestSupport {

    @Test
    void 페치_조인으로_회원을_함께_조회하면_쿼리가_1번_나간다() {
        System.out.println("===== 주문 + 회원 페치 조인 =====");
        List<Order> orders = orderRepository.findAllWithMemberFetchJoin();

        System.out.println("===== 회원 이름 접근 =====");
        for (Order order : orders) {
            // 조인으로 이미 채워진 실제 객체 → 추가 SELECT 없음
            System.out.println(order.getMember().getName());
        }

        assertThat(queryCount()).isEqualTo(1);
    }

    @Test
    void 페치_조인으로_주문상품을_함께_조회하면_쿼리가_1번_나간다() {
        System.out.println("===== 주문 + 주문상품 페치 조인 =====");
        List<Order> orders = orderRepository.findAllWithOrderItemsFetchJoin();

        System.out.println("===== 주문상품 접근 =====");
        for (Order order : orders) {
            System.out.println(order.getId() + "번 주문 상품 수: " + order.getOrderItems().size());
        }

        assertThat(queryCount()).isEqualTo(1);
        // 조인 결과는 주문상품 기준 10행이지만, 주문은 중복 없이 5개
        assertThat(orders).hasSize(ORDER_COUNT);
    }
}
