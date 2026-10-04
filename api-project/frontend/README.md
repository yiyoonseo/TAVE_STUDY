# 티끌저금통 (Dustbank) 프런트엔드

> 부모와 자녀가 함께 쓰는 가족 용돈 관리 웹 애플리케이션. Spring Boot API 서버(`../dustbank`)와 연동한다.

## 기술 스택

Vue 3 (Composition API) · Vite · Pinia · Vue Router · Axios · Chart.js(vue-chartjs)

## 실행 방법

1. 인프라: MySQL(3308), Redis(6379) 컨테이너 실행 (`../dustbank/docker-compose.yml`)
2. 백엔드: `../dustbank`를 `local` 프로파일로 실행 (포트 8080)
3. 테스트 데이터 (빈 DB에서 한 번): `node ../seed/seed.mjs`
4. 프런트엔드

```sh
npm install
npm run dev     # http://localhost:5173
```

`/api` 요청은 Vite 개발 서버가 `http://localhost:8080`으로 전달하므로 CORS 설정이 필요 없다.

## 화면과 API

| 화면 | 경로 | 사용하는 API |
|---|---|---|
| 로그인 | `/` | `GET /parents/{id}` |
| 회원가입 | `/register` | `POST /parents` |
| 가족 | `/family` | 자녀 목록, 자녀 삭제 |
| 부모 대시보드 | `/parent` | 자녀 목록(잔액) |
| 자녀 추가 | `/parent/add-child` | `POST /parents/{id}/children` |
| 용돈 주기 | `/parent/children/:id/income` | 자녀 조회, `POST .../incomes` |
| 지출 등록 | `/child/:id/expenditure` | `POST .../expenses` |
| 거래 내역(+수정 모달) | `/parent\|child/.../transactions` | 거래 목록(`?type=`), 조회·수정·삭제 |
| 소비 통계 | `/parent/children/:id/stats`, `/child/:id` | 자녀 조회, 주간 통계 |

로그인한 부모 정보는 `localStorage`에 저장되어 새로고침해도 유지되고, 로그인하지 않으면 홈으로 이동한다.

## 구조

```
src/
├─ api/           axios 인스턴스(서버 에러 메시지 추출) + 리소스별 API 함수
├─ constants/     서버 enum ↔ 화면 라벨 (수입 유형, 지출 카테고리)
├─ utils/         금액·날짜 포맷, 아이콘 경로
├─ stores/        session (로그인한 부모)
├─ components/    AppNav, DoughnutChart, Stats, 거래 목록·항목, 사용자 카드
├─ pages/         화면 (부모/자녀 공용 페이지는 라우트 meta.role로 구분)
└─ router/        라우트 + 로그인 가드
```

## 기존(json-server) 대비 개선

- 잔액 계산, 자녀 삭제 시 거래 삭제, 주간 통계 계산을 프런트에서 하던 방식을 서버 API로 대체
- 부모 ID 하드코딩(`momdad`, `gildong`)을 제거하고 로그인 정보를 한 곳(`session`)에서 관리
- 중복 코드 통합: 상단 메뉴(2→1), 거래 내역 페이지(2→1), 통계 페이지(2→1), 도넛 차트(2→1)
- 분류 값을 서버 enum으로 통일하고 상수 파일 하나에서 관리 (세뱃돈 분류 제거)
- 날짜·시간 입력을 `datetime-local` 하나로 단순화, 시간대 없는 로컬 시각 문자열로 전송
- `alert` 대신 서버가 내려준 에러 메시지를 화면에 표시
- 반응형: 고정 폭(1260px 등) 레이아웃을 유동 폭으로 바꾸고 모바일(375px)·태블릿(768px)·데스크톱(1440px)에서 가로 넘침 없이 확인
- 수정 모달의 전역 스타일(`.category`, `.memo` 등)이 다른 화면에 적용되던 문제를 `scoped`로 수정
