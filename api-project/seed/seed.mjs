// 테스트 데이터 입력 스크립트: 기존 db.json(json-server 데이터)을 새 API로 옮긴다.
//
// 사용법 (Spring 서버를 local 프로파일로 띄운 뒤, 빈 DB에서 한 번 실행)
//   node seed/seed.mjs [API 주소]        기본값: http://localhost:8080/api/v1
//
// db.json 데이터를 현재 상황에 맞게 바꾸는 규칙
//  - 잔액은 db.json의 저장값을 쓰지 않는다. 서버가 거래를 넣으면서 계산한다.
//  - 가장 최근 거래가 "어제"가 되도록 모든 날짜를 같은 일수만큼 옮긴다 (이번 주 통계에 데이터가 보이게).
//  - 분류 값은 서버 enum으로 바꾼다 (예: 정기용돈 → REGULAR, 간식 → SNACK, N → NEED).
import { readFileSync } from 'node:fs';

const BASE_URL = process.argv[2] ?? 'http://localhost:8080/api/v1';
const db = JSON.parse(
  readFileSync(new URL('./db.json', import.meta.url), 'utf-8'),
);

const INCOME_TYPE = { 정기용돈: 'REGULAR', 보너스: 'BONUS', 기타: 'ETC' };
const EXPENSE_CATEGORY = {
  식사: 'MEAL',
  간식: 'SNACK',
  장난감: 'TOY',
  취미: 'HOBBY',
  준비물: 'SUPPLIES',
  기타: 'ETC',
};
const NEED_TYPE = { N: 'NEED', W: 'WANT' };

// ---- 날짜 계산: db.json은 UTC(Z), 서버는 시간대 없는 서울 시각 문자열을 받는다 ----
const KST_OFFSET = 9 * 60 * 60 * 1000;
const DAY = 24 * 60 * 60 * 1000;
const pad = (n) => String(n).padStart(2, '0');
// UTC getter로 읽으면 서울 시각이 나오도록 9시간을 더한 Date
const toKst = (iso) => new Date(new Date(iso).getTime() + KST_OFFSET);
const dayStart = (d) =>
  Date.UTC(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate());
const format = (d) =>
  `${d.getUTCFullYear()}-${pad(d.getUTCMonth() + 1)}-${pad(d.getUTCDate())}` +
  `T${pad(d.getUTCHours())}:${pad(d.getUTCMinutes())}:${pad(d.getUTCSeconds())}`;

const transactions = [...db.transactions].sort((a, b) =>
  a.date.localeCompare(b.date),
);
const lastDay = dayStart(toKst(transactions.at(-1).date));
const yesterday = dayStart(new Date(Date.now() + KST_OFFSET)) - DAY;
const shiftDays = Math.round((yesterday - lastDay) / DAY);

const shifted = (iso) => format(new Date(toKst(iso).getTime() + shiftDays * DAY));

// ---- API 호출 ----
async function call(method, path, body) {
  const response = await fetch(BASE_URL + path, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await response.text();
  if (!response.ok) {
    throw new Error(`${method} ${path} → ${response.status} ${text}`);
  }
  return text ? JSON.parse(text) : null;
}

async function main() {
  // 1) 부모
  const parent = db.parents[0];
  try {
    await call('POST', '/parents', {
      parentId: parent.id,
      nickname: parent.nickname,
      iconId: parent.iconId,
    });
  } catch (error) {
    if (String(error.message).includes('409')) {
      console.error(
        `부모 '${parent.id}'가 이미 있습니다. DB를 비운 뒤 다시 실행하세요.`,
      );
      process.exit(1);
    }
    throw error;
  }
  console.log(`부모 등록: ${parent.id}`);

  // 2) 자녀 (db.json의 문자열 id → 서버가 만든 숫자 id)
  const childIds = {};
  for (const child of db.children) {
    const created = await call('POST', `/parents/${parent.id}/children`, {
      nickname: child.nickname,
      iconId: child.iconId,
    });
    childIds[child.id] = created.childId;
    console.log(`자녀 등록: ${child.nickname} (id ${created.childId})`);
  }

  // 3) 거래: 시간순으로 넣어야 잔액이 음수가 되지 않는다
  for (const t of transactions) {
    const childId = childIds[t.childId];
    const common = {
      amount: t.amount,
      // 날짜를 옮겼으므로 "3월 용돈" 같은 낡은 표현은 바꾼다
      memo: t.memo.replace('3월 용돈', '첫 용돈'),
      transactedAt: shifted(t.date),
    };
    if (t.type === 'I') {
      await call('POST', `/children/${childId}/incomes`, {
        ...common,
        incomeType: INCOME_TYPE[t.category3] ?? 'ETC',
      });
    } else {
      await call('POST', `/children/${childId}/expenses`, {
        ...common,
        needType: NEED_TYPE[t.category1],
        expenseCategory: EXPENSE_CATEGORY[t.category2] ?? 'ETC',
      });
    }
  }
  console.log(`거래 ${transactions.length}건 등록 (날짜를 ${shiftDays}일 이동)`);

  // 4) 결과 확인
  const children = await call('GET', `/parents/${parent.id}/children`);
  for (const child of children) {
    console.log(`  ${child.nickname}: 잔액 ${child.balance.toLocaleString()}원`);
  }
}

main().catch((error) => {
  console.error(error.message);
  process.exit(1);
});
