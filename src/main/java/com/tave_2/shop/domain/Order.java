package com.tave_2.shop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // ORDER는 SQL 예약어라 테이블명을 바꿔줌
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    // N+1 발생 지점 1: 주문 목록을 조회한 뒤 order.getMember()에 접근할 때
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // N+1 발생 지점 2: 주문 목록을 조회한 뒤 order.getOrderItems()에 접근할 때
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    public Order(Member member) {
        this.member = member;
        this.orderDate = LocalDateTime.now();
        member.getOrders().add(this);
    }

    // 연관관계 편의 메서드: 양쪽 객체를 한 번에 세팅
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
