package com.example.nplus1;

import com.example.nplus1.domain.Order;
import com.example.nplus1.domain.OrderItem;
import com.example.nplus1.domain.Product;
import com.example.nplus1.repository.OrderItemRepository;
import com.example.nplus1.repository.OrderRepository;
import com.example.nplus1.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Nplus1Runner implements CommandLineRunner {

    // CommandLineRunner -> Spring Boot가 실행된 직후 특정 코드를 한 번 실행하게 해주는 인터페이스

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final EntityManager em;

    @Override
    @Transactional // 필수! JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
    public void run(String... args) throws Exception {

        // Product 테스트 데이터 만들기
        Product laptop = new Product("노트북", 1_500_000);
        Product mouse = new Product("마우스", 50_000);
        Product keyboard = new Product("키보드", 100_000);
        Product monitor = new Product("모니터", 300_000);
        Product earphone = new Product("이어폰", 200_000);
        Product charger = new Product("충전기", 30_000);

        productRepository.save(laptop);
        productRepository.save(mouse);
        productRepository.save(keyboard);
        productRepository.saveAll(List.of(monitor, earphone, charger));

        // Order 테스트 데이터 만들기
        Order order1 = new Order(LocalDateTime.now());
        Order order2 = new Order(LocalDateTime.now());
        Order order3 = new Order(LocalDateTime.now());

        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        // OrderItem 테스트 데이터 만들기
        OrderItem item1 = new OrderItem(1, order1, laptop);
        OrderItem item2 = new OrderItem(1, order1, mouse);
        OrderItem item3 = new OrderItem(1, order2, keyboard);
        OrderItem item4 = new OrderItem(2, order2, monitor);
        OrderItem item5 = new OrderItem(1, order3, earphone);
        OrderItem item6 = new OrderItem(2, order3, charger);

        orderItemRepository.saveAll(List.of(item1, item2, item3, item4, item5, item6));

        em.flush(); // 지금까지의 INSERT를 DB에 반영
        em.clear(); // 1차 캐시를 완전히 비우기 (N+1 문제를 일으키키 위해)

        // N+1 발생시키기
        System.out.println("\n============N+1 문제 테스트 시작============");

        System.out.println("\n[1] OrderItem 전체 조회");
        List<OrderItem> orderItems = orderItemRepository.findAll(); // 아직 Product 내용은 가져오지 않음 (1)

        System.out.println("\n[2] 각 OrderItem의 Product 접근");
        int count = 1;
        for (OrderItem orderItem : orderItems) {
            System.out.println("\n----------Product #" + count + "----------");
            String productName = orderItem.getProduct().getName(); // 실제 Product 정보 필요 (N)
            System.out.println(">>> 상품명: " + productName);
            count++;
        }

        System.out.println("\n============N+1 문제 테스트 종료============");

        em.clear(); // fetch join 테스트를 위해 영속성 컨텍스트의 1차 캐시 비우기

        // 1. fetch join
        System.out.println("\n============Fetch Join 테스트 시작============");

        System.out.println("\n[1] OrderItem + Product Fetch Join 조회");
        List<OrderItem> fetchJoinItems = orderItemRepository.findAllWithProduct();

        System.out.println("\n[2] 각 OrderItem의 Product 접근");
        count = 1;
        for (OrderItem orderItem : fetchJoinItems) {
            System.out.println("\n----------Product #" + count + "----------");
            String productName = orderItem.getProduct().getName();
            System.out.println(">>> 상품명: " + productName);
            count++;
        }

        System.out.println("\n============Fetch Join 테스트 종료============");

    }
}
