import { apiFetch } from './api';

export async function getMe() {
  return apiFetch('/me');
}

export async function changeMyPassword(newPassword) {
  return apiFetch('/me/password', {
    method: 'PATCH',
    body: JSON.stringify({ newPassword }),
  });
}