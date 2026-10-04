import http from './http';

export const fetchParent = (parentId) =>
  http.get(`/parents/${encodeURIComponent(parentId)}`).then((res) => res.data);

export const createParent = (body) =>
  http.post('/parents', body).then((res) => res.data);
