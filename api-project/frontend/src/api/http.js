import axios from 'axios';

const http = axios.create({ baseURL: '/api/v1' });

// 서버 에러 응답({status, code, message})의 message를 꺼내 Error로 던진다.
// 그래서 화면에서는 catch한 error.message만 보여주면 된다.
http.interceptors.response.use(
  (response) => response,
  (error) => {
    const message =
      error.response?.data?.message ?? '서버와 통신하는 중 문제가 발생했습니다.';
    return Promise.reject(new Error(message));
  },
);

export default http;
