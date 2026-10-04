# 티끌저금통 API 서버 (캐싱 · 프로파일 · Actuator)

부모가 자녀에게 용돈을 주고, 자녀가 지출을 기록하는 **가족 용돈 관리 서비스**의 API 서버를 만들었다.  
기존에는 `json-server`로 대체한 Vue 프런트엔드만 있었고 잔액·통계 계산이 전부 프런트 코드에 있었다.  
기능은 유지한 채 Spring Boot 서버로 옮기고, **Redis 캐싱, local/prod 프로파일 분리, Actuator 헬스체크**를 적용한 뒤 프런트를 새 서버에 연동했다.

## 개발 환경

- Java 17
- Spring Boot 4.1.1
- Spring Data JPA, Hibernate, MySQL 9.3 (Docker)
- Spring Cache, Redis 7 (Docker)
- Spring Boot Actuator
- Gradle
- 프런트엔드: Vue 3, Vite, Pinia, Vue Router, Axios, Chart.js (Node 20.19+ / 22.12+)

## 프로젝트 구조

```text
api-project/
├─ dustbank/    Spring Boot API 서버 (+ docker-compose.yml)
├─ frontend/    Vue 프런트엔드 (자세한 내용은 frontend/README.md)
└─ seed/        테스트 데이터 입력 스크립트 (기존 db.json 기반)
```

```text
Controller (요청·응답) → Service (규칙·트랜잭션) → Repository (JPA) → MySQL
        └ DTO(record)                                  └ 통계 조회만 Redis 캐시
```

---

## 실행 방법

### 1. 인프라 (MySQL 3308, Redis 6379)

```bash
cd dustbank
docker compose up -d
```

### 2. 백엔드 (local 프로파일, 포트 8080)

```bash
cd dustbank
./gradlew bootRun
```

### 3. 테스트 데이터 (빈 DB에서 한 번)

```bash
node seed/seed.mjs
```

### 4. 프런트엔드 (포트 5173)

```bash
cd frontend
npm install
npm run dev
```

로그인 ID는 `momdad`이다.

### prod 프로파일로 실행

prod는 별도 DB(`dustbank_prod`)를 쓰고, 테이블을 만들지 않고 검사만 한다(`ddl-auto: validate`).  
그래서 처음에 DB를 만들고 테이블 구조를 복사해 둔다.

```bash
docker exec dustbank-mysql-1 mysql -uroot -proot -e "CREATE DATABASE dustbank_prod CHARACTER SET utf8mb4;"
docker exec dustbank-mysql-1 mysqldump -uroot -proot --no-data dustbank_local | docker exec -i dustbank-mysql-1 mysql -uroot -proot dustbank_prod
```

DB 비밀번호는 코드에 두지 않고 환경변수로 받는다.

```bash
cd dustbank
DB_PASSWORD=root ./gradlew bootRun --args='--spring.profiles.active=prod'    # 포트 8081 (Git Bash)
```

```powershell
$env:DB_PASSWORD = 'root'; .\gradlew bootRun --args='--spring.profiles.active=prod'   # PowerShell
```

---

## API

모든 경로는 `/api/v1` 아래에 있다. URL은 "무엇(리소스)"을 나타내고, 목록·생성은 소유자 밑, 이미 있는 1건은 자기 ID로 접근한다.

| 리소스 | 메서드 | 경로 | 설명 |
|---|---|---|---|
| 부모 | POST | `/parents` | 회원가입 |
| | GET | `/parents/{parentId}` | 로그인(존재 확인) |
| 자녀 | GET | `/parents/{parentId}/children` | 자녀 목록 |
| | POST | `/parents/{parentId}/children` | 자녀 등록 |
| | GET | `/children/{childId}` | 자녀 조회(잔액 포함) |
| | DELETE | `/children/{childId}` | 자녀 삭제(거래 함께 삭제) |
| 거래 | GET | `/children/{childId}/transactions` | 거래 목록 (`type`, `from`, `to` 필터) |
| | POST | `/children/{childId}/incomes` | 용돈 지급(수입) |
| | POST | `/children/{childId}/expenses` | 지출 등록 |
| | GET · PUT · DELETE | `/transactions/{transactionId}` | 거래 조회 · 수정 · 삭제 |
| 통계 | GET | `/children/{childId}/stats/weekly` | 주간 통계 (캐싱 대상) |
| 헬스체크 | GET | `/actuator/health` | DB·Redis 상태 |

에러는 `{status, code, message}` 형식으로 통일했다 (`400` 입력 오류·잔액 부족, `404` 없음, `409` 중복).

---

## 잔액 정합성

잔액은 자녀 테이블에 저장하고, 거래 저장과 잔액 변경을 **한 트랜잭션**에서 처리한다.

```java
@Transactional
public TransactionResponse createExpense(Long childId, ExpenseCreateRequest request) {
    ...
    child.decreaseBalance(request.amount());   // 잔액이 부족하면 예외 → 전체 롤백
    transactionRepository.save(transaction);
}
```

| 동작 | 잔액 변화 |
|---|---|
| 수입 / 지출 등록 | `+금액` / `−금액` |
| 수정 | 차액만큼 |
| 삭제 | 수입은 `−금액`, 지출은 `+금액` |

어떤 경우든 잔액이 음수가 되면 `INSUFFICIENT_BALANCE`(400)로 거절하고 롤백한다.  
잔액 부족 요청 전후로 거래 건수와 잔액이 변하지 않았고, 수정·삭제를 여러 번 거친 뒤에도 `잔액 = 거래 합계`가 유지됐다.

---

## 캐싱 (Redis)

**주간 통계만 캐싱했다.** 한 주의 거래를 모두 읽어 합산하는 무거운 읽기이고 대시보드에서 반복 조회되기 때문이다.  
자녀 조회는 조회 비용이 작고, 자녀 목록은 잔액이 바뀔 때마다 비워야 해서 이득 대비 무효화 비용이 커 제외했다.

```java
@Cacheable(value = "weeklyStats",
        key = "#childId + '::' + (#date != null ? #date : T(java.time.LocalDate).now())")
public WeeklyStatsResponse getWeekly(Long childId, LocalDate date) { ... }

@CacheEvict(value = "weeklyStats", allEntries = true)   // 거래 등록·수정·삭제, 자녀 삭제
```

- 키는 `weeklyStats::{childId}::{날짜}`. 날짜를 생략하면 오늘 날짜를 키에 넣어 주가 바뀌면 새 키가 쓰인다.
- 영향받는 키를 특정하기 어려워 `allEntries = true`로 비운다. 다른 자녀의 캐시도 지워지지만 다음 조회 때 한 번 다시 계산하면 된다.
- 예외가 난 요청(잔액 부족 등)은 캐시를 지우지 않는다.
- 쓰기 로직은 캐시가 아니라 DB를 읽으므로, 캐시가 틀려도 돈 계산은 어긋나지 않는다.
- JSON으로 직렬화하고, 복원할 수 있는 클래스를 이 프로젝트의 DTO와 기본 컬렉션·날짜 타입으로 제한했다.

| 확인 | 결과 |
|---|---|
| 같은 통계를 반복 조회 | 첫 호출만 DB 조회, 이후 SQL 0건 |
| 수입 등록 직후 통계 조회 | 즉시 최신 값 (무효화 전에는 TTL이 끝날 때까지 옛 값) |
| 잔액 부족 등 실패한 요청 | 캐시 유지 |
| 없는 자녀(404) | 예외는 캐시에 저장되지 않음 |

---

## 프로파일 (local / prod)

| 항목 | local | prod |
|---|---|---|
| DB | `dustbank_local` | `dustbank_prod` |
| DB 비밀번호 | yml | 환경변수 `${DB_PASSWORD}` |
| Redis DB 번호 | 0 | 1 |
| 캐시 TTL | 60초 | 600초 |
| `ddl-auto` / SQL 로그 | `update` / 출력 | `validate` / 출력 안 함 |
| 포트 | 8080 | 8081 |
| Actuator 노출 | 전체 | `health`, `info`만 |
| 헬스 상세 | 표시 | 숨김 |

```text
application.yml                (공통, 기본 프로파일 = local)
├─ application-local.yml
└─ application-prod.yml        ← --spring.profiles.active=prod
```

- 설정 우선순위는 실행 인자 > 환경변수 > yml
- `DB_PASSWORD` 환경변수 없이 prod를 실행하면 Spring이 `${DB_PASSWORD}`를 해석하지 못하고 **문자열 그대로** 비밀번호로 전달해서, 기동은 실패하지만 오류가 "비밀번호 틀림"(`Access denied`)으로 보인다.

---

## Actuator 헬스체크

`/actuator/health`는 DB와 Redis 연결까지 검사하고, 하나라도 죽으면 전체가 `DOWN`(503)이 된다.

| | local | prod |
|---|---|---|
| `/actuator/health` | `db`, `redis` 등 상세 포함 | `{"status":"UP"}`만 |
| `/actuator/metrics`, `env`, `beans` | 200 | 404 (노출 안 함) |

Redis 컨테이너를 멈추면 헬스체크가 **503 `DOWN`**이 되고 복구하면 `UP`으로 돌아온다.  
다만 Redis 연결 대기 시간(기본 60초) 때문에 `DOWN` 응답까지 약 60초가 걸렸고, 같은 시점의 `health/readiness`는 `UP`이었다(기본 설정에서는 readiness 그룹에 Redis가 포함되지 않음).

---

## 프런트엔드 연동

기존 프런트를 복사해 새 API에 연동하면서 구조를 정리했다. (실행 방법과 구조는 [frontend/README.md](frontend/README.md))

- 잔액·통계 계산, 자녀 삭제 시 거래 삭제를 프런트에서 하던 방식을 서버 API로 대체
- 부모 ID 하드코딩을 없애고 로그인 정보를 한 곳(`session`)에서 관리, 새로고침해도 유지
- 중복되던 상단 메뉴·거래 내역·통계 페이지·도넛 차트를 각각 하나로 통합
- 서버가 내려주는 에러 메시지를 그대로 화면에 표시, 모바일·태블릿·데스크톱 반응형 적용

기존 `db.json`은 저장된 잔액(83,400원)과 거래 합계(89,900원)가 서로 맞지 않았다. 프런트가 잔액을 따로 갱신하던 방식의 한계가 드러난 사례였고, 새 서버가 계산한 값으로 바로잡혔다.

---

## 한계

- 같은 자녀에게 동시에 요청이 들어올 때 잔액 갱신의 잠금 처리는 하지 않았다.
- 거래 목록 페이징과 인증·권한은 범위에서 제외했다.
- 캐시 무효화가 `allEntries`라 다른 자녀의 캐시도 함께 삭제된다.
- 캐시 효과를 수치로 비교하는 부하 테스트는 하지 않았다.
