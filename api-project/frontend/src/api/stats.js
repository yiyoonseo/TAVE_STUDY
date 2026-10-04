import http from './http';

export const fetchWeeklyStats = (childId) =>
  http.get(`/children/${childId}/stats/weekly`).then((res) => res.data);
