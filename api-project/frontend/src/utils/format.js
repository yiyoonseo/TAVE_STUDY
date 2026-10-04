// 금액: 1234567 → '1,234,567'
export const formatMoney = (amount) => Number(amount ?? 0).toLocaleString('ko-KR');

const pad = (n) => String(n).padStart(2, '0');

// 서버가 주는 'YYYY-MM-DDTHH:mm:ss...' → '2026.03.10 09:00'
export const formatDateTime = (value) => {
  const d = new Date(value);
  return `${d.getFullYear()}.${pad(d.getMonth() + 1)}.${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

// 서버가 주는 값 → '2026년 3월 10일 (화)' (거래 목록의 날짜 묶음 제목)
export const formatDateTitle = (value) =>
  new Date(value).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'short',
  });

// <input type="datetime-local">의 값은 'YYYY-MM-DDTHH:mm' 형식이다.
export const nowForInput = () => {
  const d = new Date();
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

// 서버 값 → input 값 ('2026-03-10T09:00:00' → '2026-03-10T09:00')
export const toInputValue = (value) => String(value).slice(0, 16);

// input 값 → 서버로 보낼 값. 시간대(Z)를 붙이지 않은 로컬 시각 문자열
export const toApiValue = (inputValue) => `${inputValue}:00`;

// 아이콘 이미지 경로. 부모/자녀 아이콘은 icons 폴더, 자녀 등록 화면의 선택용은 icons2 폴더
export const iconPath = (iconId, folder = 'icons') =>
  new URL(`../assets/${folder}/icon${iconId}.png`, import.meta.url).href;
