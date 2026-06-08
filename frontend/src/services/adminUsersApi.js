import { apiFetch } from './api';

export async function getAdminUsers() {
  return apiFetch('/admin/users');
}

export async function getAssignableUsers() {
  return apiFetch('/admin/users/assignable');
}

export async function createAdminUser(payload) {
  return apiFetch('/admin/users', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function updateAdminUser(userId, payload) {
  return apiFetch(`/admin/users/${userId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
}

export async function enableAdminUser(userId) {
  return apiFetch(`/admin/users/${userId}/enable`, {
    method: 'PATCH',
  });
}

export async function disableAdminUser(userId) {
  return apiFetch(`/admin/users/${userId}/disable`, {
    method: 'PATCH',
  });
}

export async function resetAdminUserPassword(userId, password) {
  return apiFetch(`/admin/users/${userId}/reset-password`, {
    method: 'PATCH',
    body: JSON.stringify({ password }),
  });
}

export async function getSellerUsers() {
  return apiFetch('/admin/users/sellers');
}