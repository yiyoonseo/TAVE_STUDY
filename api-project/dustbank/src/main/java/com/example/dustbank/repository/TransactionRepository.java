package com.example.dustbank.repository;

import com.example.dustbank.domain.Transaction;
import com.example.dustbank.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /*
    특정 자녀의 거래 목록을 조건에 맞게 조회 (type, from, to는 선택 조건)
    - JPQL: 테이블이 아니라 엔티티/필드 이름으로 쓰는 쿼리. Transaction은 transactions 테이블, t.child.childId는 child_id 컬럼
    - :childId 처럼 콜론이 붙은 것은 메서드 파라미터가 들어가는 자리 (@Param 이름과 같아야 함)
    - (:type is null or t.type = :type)
        type이 null이면 앞쪽이 true라서 이 조건은 통과(조건 없음), 값이 있으면 t.type = :type으로 필터링
        from, to도 같은 방식이라 넘기지 않으면 전체 기간을 조회
    - t.transactedAt < :to
        종료일은 미만(<)으로 비교. 서비스에서 "종료일 다음 날 0시"를 넘기기 때문에 종료일 하루 전체가 포함됨
    - order by: 최신 거래순, 같은 시각이면 transactionId가 큰 것(나중에 만든 것)부터
    */
    @Query("select t from Transaction t "
            + "where t.child.childId = :childId "
            + "and (:type is null or t.type = :type) "
            + "and (:from is null or t.transactedAt >= :from) "
            + "and (:to is null or t.transactedAt < :to) "
            + "order by t.transactedAt desc, t.transactionId desc")
    List<Transaction> search(@Param("childId") Long childId,
                             @Param("type") TransactionType type,
                             @Param("from") LocalDateTime from,
                             @Param("to") LocalDateTime to);

    void deleteByChild_ChildId(Long childId);
}
