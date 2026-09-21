package com.tave_2.shop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // N+1 발생 지점 3: orderItem.getItem()에 접근할 때
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;
    private int count;

    public OrderItem(Item item, int count) {
        this.item = item;
        this.orderPrice = item.getPrice();
        this.count = count;
    }

    // Order.addOrderItem()에서만 호출
    void setOrder(Order order) {
        this.order = order;
    }
}
