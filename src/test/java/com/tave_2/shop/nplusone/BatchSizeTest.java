package com.tave_2.shop.nplusone;

import com.tave_2.shop.domain.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * default_batch_fetch_size를 이 테스트에서만 켬
 * (application.yaml에서 전역으로 켜두면 NPlusOneTest에서 N+1이 재현되지 않음)
 */
@TestPropertySource(properties = "spring.jpa.properties.hibernate.default_batch_fetch_size=100")
class BatchSizeTest extends NPlusOneTestSupport {

    @Test
    void 배치_사이즈를_설정하면_회원을_한_번에_묶어서_조회한다() {
        System.out.println("===== 주문 목록 조회 =====");
        List<Order> orders = orderRepository.findAll(); // N+1 테스트와 똑같은 쿼리

        System.out.println("===== 회원 이름 접근 =====");
        for (Order order : orders) {
            // 첫 접근 시 나머지 프록시의 회원까지 한 번에 조회
            System.out.println(order.getMember().getName());
        }

        // 주문 조회 1번 + 회원 묶음 조회 1번
        assertThat(queryCount()).isEqualTo(2);
    }

    @Test
    void 배치_사이즈를_설정하면_주문상품을_한_번에_묶어서_조회한다() {
        System.out.println("===== 주문 목록 조회 =====");
        List<Order> orders = orderRepository.findAll();

        System.out.println("===== 주문상품 접근 =====");
        for (Order order : orders) {
            System.out.println(order.getId() + "번 주문 상품 수: " + order.getOrderItems().size());
        }

        // 주문 조회 1번 + 주문상품 묶음 조회 1번
        assertThat(queryCount()).isEqualTo(2);
    }
}
