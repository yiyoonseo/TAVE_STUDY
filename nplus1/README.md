# JPA N+1 문제 실습

쇼핑몰 도메인의 연관관계를 구현하고 N+1 문제를 직접 발생시킨 뒤,  
Fetch Join, `@EntityGraph`, Batch Size를 적용해 해결 방법별 쿼리 차이를 비교했다.

## 개발 환경

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA
- Hibernate
- H2 Database
- Maven

## 도메인 구조

```text
Order 1 <── N OrderItem N ──> 1 Product
```

`OrderItem`에서 `Order`, `Product`를 참조하는 단방향 연관관계로 구성했다.

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "order_id")
private Order order;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "product_id")
private Product product;
```

---

## N+1 문제 발생

`OrderItem` 전체를 조회한 뒤 각 `OrderItem`의 `Product`에 접근하도록 구현했다.

```java
List<OrderItem> orderItems = orderItemRepository.findAll();

for (OrderItem orderItem : orderItems) {
    String productName = orderItem.getProduct().getName();
}
```

`Product`가 `LAZY`로 설정되어 있기 때문에 `OrderItem` 조회 시에는 `Product`의 실제 데이터를 조회하지 않는다.

이후 각 `Product`에 접근하는 시점에 지연 로딩을 위한 추가 SELECT가 발생한다. 
이번 테스트에서는 6개의 `OrderItem`이 서로 다른 `Product`를 참조하도록 구성해 총 6번의 추가 조회가 발생했다.

```text
OrderItem 전체 조회  → 1번
Product 개별 조회    → 6번

총 7번의 SELECT 발생
```

테스트 전에 `EntityManager.clear()`를 호출하여 1차 캐시의 영향을 제거하고 N+1 발생을 확인했다.

---

## 해결 방법

### 1. Fetch Join

JPQL의 Fetch Join을 사용해 `OrderItem`을 조회할 때 `Product`도 함께 조회했다.

```java
@Query("""
        select oi
        from OrderItem oi
        join fetch oi.product
        """)
List<OrderItem> findAllWithProduct();
```

```text
OrderItem + Product 조회   → 1번
Product 접근 시 추가 조회    → 0번
```

엔티티의 `LAZY` 설정은 유지하면서 특정 조회에서만 연관 엔티티를 함께 가져온다.

---

### 2. @EntityGraph

`@EntityGraph`를 사용해 조회 시 함께 로딩할 연관관계를 지정했다.

```java
@EntityGraph(attributePaths = "product")
@Query("select oi from OrderItem oi")
List<OrderItem> findAllWithEntityGraph();
```

```text
OrderItem + Product 조회   → 1번
Product 접근 시 추가 조회    → 0번
```

Fetch Join과 달리 JPQL에 Fetch 전략을 직접 작성하지 않고,  
어노테이션을 통해 함께 조회할 연관관계를 지정할 수 있다.

---

### 3. Batch Size

`LAZY`는 그대로 유지하면서 여러 Product를 일정 개수씩 묶어 조회하도록 설정했다.  
일반적으로는 `application.properties`에 `spring.jpa.properties.hibernate.default_batch_fetch_size`를 설정해 Batch Size를 전역 적용할 수 있다.   
이번 실습에서는 기존 N+1 테스트에 영향을 주지 않도록 Batch Size 테스트에서만 `session.setFetchBatchSize(3)`을 적용했다.
```java
Session session = em.unwrap(Session.class);
session.setFetchBatchSize(3);
```

실습에서는 Batch Size를 `3`으로 설정해 6개의 `Product`가 두 번에 나뉘어 조회되는 것을 확인했다.

```text
OrderItem 전체 조회        → 1번
Product 3개씩 Batch 조회   → 2번

총 3번의 SELECT 발생
```

Batch Size는 Fetch Join이나 `@EntityGraph`처럼 한 번의 JOIN으로 조회하는 것이 아니라,  
지연 로딩(`LAZY`) 시 발생하는 개별 조회를 여러 건씩 묶어 쿼리 수를 줄이는 방식이다.

---

## 결과 비교

| 방식 | 조회 방식 | SELECT 횟수 |
| --- | --- | ---: |
| 기본 LAZY | `OrderItem` 1번 + `Product` 개별 조회 | 7 |
| Fetch Join | `OrderItem`과 `Product`를 JOIN하여 조회 | 1 |
| `@EntityGraph` | `Product`를 함께 로딩하도록 지정 | 1 |
| Batch Size = 3 | `Product`를 3개씩 묶어서 조회 | 3 |

### 정리

- **Fetch Join**
    - JPQL에서 `join fetch`를 직접 작성
    - 필요한 연관 엔티티를 한 번의 쿼리로 함께 조회

- **`@EntityGraph`**
    - 조회 쿼리와 Fetch 전략을 분리
    - 어노테이션으로 함께 조회할 연관관계를 지정

- **Batch Size**
    - LAZY Loading 유지
    - 연관 엔티티 조회를 일정 개수씩 묶어 쿼리 수 감소

---

## 실행 로그

N+1 발생 및 각 해결 방식의 실제 SQL은 아래 파일에서 확인할 수 있다.

[실행 로그 확인](docs/nplus1-query-log.txt)