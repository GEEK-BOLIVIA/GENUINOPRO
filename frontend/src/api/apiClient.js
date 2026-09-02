const API_BASE_URL =
  import.meta.env.VITE_API_URL || '/api';

let authToken = null;

export function setApiToken(token) {
  authToken = token || null;
}

export function getApiToken() {
  return authToken;
}

function buildHeaders(options = {}) {
  return {
    'Content-Type': 'application/json',
    ...(authToken ? { Authorization: `Bearer ${authToken}` } : {}),
    ...(options.headers || {}),
  };
}

async function doFetch(endpoint, options = {}) {
  return fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: buildHeaders(options),
  });
}

export async function apiFetch(endpoint, options = {}) {
  const response = await doFetch(endpoint, options);

  if (response.status === 401) {
    authToken = null;

    window.location.reload();

    throw new Error('Sesión expirada');
  }

  if (!response.ok) {
    const message = await response.text();

    throw new Error(message || `Error API ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();

  if (!text) {
    return null;
  }

  return JSON.parse(text);
}