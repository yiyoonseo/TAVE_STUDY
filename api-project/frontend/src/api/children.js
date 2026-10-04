import http from './http';

export const fetchChildren = (parentId) =>
  http
    .get(`/parents/${encodeURIComponent(parentId)}/children`)
    .then((res) => res.data);

export const createChild = (parentId, body) =>
  http
    .post(`/parents/${encodeURIComponent(parentId)}/children`, body)
    .then((res) => res.data);

export const fetchChild = (childId) =>
  http.get(`/children/${childId}`).then((res) => res.data);

export const deleteChild = (childId) => http.delete(`/children/${childId}`);
