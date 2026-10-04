import http from './http';

// type: 'INCOME' | 'EXPENSE' | undefined(전체)
export const fetchTransactions = (childId, type) =>
  http
    .get(`/children/${childId}/transactions`, { params: { type } })
    .then((res) => res.data);

export const fetchTransaction = (transactionId) =>
  http.get(`/transactions/${transactionId}`).then((res) => res.data);

export const createIncome = (childId, body) =>
  http.post(`/children/${childId}/incomes`, body).then((res) => res.data);

export const createExpense = (childId, body) =>
  http.post(`/children/${childId}/expenses`, body).then((res) => res.data);

export const updateTransaction = (transactionId, body) =>
  http.put(`/transactions/${transactionId}`, body).then((res) => res.data);

export const deleteTransaction = (transactionId) =>
  http.delete(`/transactions/${transactionId}`);
