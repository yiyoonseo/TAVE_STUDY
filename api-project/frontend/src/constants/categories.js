// 서버 enum 값 ↔ 화면 라벨. 분류가 바뀌면 여기만 고치면 된다.

export const INCOME_TYPES = [
  { value: 'REGULAR', label: '정기용돈', icon: '💰' },
  { value: 'BONUS', label: '보너스', icon: '🎁' },
  { value: 'ETC', label: '기타', icon: '✨' },
];

export const EXPENSE_CATEGORIES = [
  { value: 'MEAL', label: '식사', icon: '🍚', color: 'orange' },
  { value: 'SNACK', label: '간식', icon: '🍪', color: 'yellow' },
  { value: 'TOY', label: '장난감', icon: '🧸', color: 'beige' },
  { value: 'HOBBY', label: '취미', icon: '🎨', color: 'sky' },
  { value: 'SUPPLIES', label: '준비물', icon: '✏️', color: 'green' },
  { value: 'ETC', label: '기타', icon: '✨', color: 'gray' },
];

export const NEED_TYPES = [
  { value: 'NEED', label: 'Need' },
  { value: 'WANT', label: 'Want' },
];

// 도넛 차트 색 (EXPENSE_CATEGORIES 순서와 같음)
export const CATEGORY_CHART_COLORS = [
  '#A5B4FC',
  '#93C5FD',
  '#86EFAC',
  '#FDE68A',
  '#FCA5A5',
  '#D1D5DB',
];

export const labelOf = (list, value) =>
  list.find((item) => item.value === value)?.label ?? value;
