export {
  apiFetch,
  setApiToken,
  getApiToken,
} from '../api/apiClient';

export async function downloadLclPdf(id, token) {
  const API_BASE_URL =
    import.meta.env.VITE_API_URL || '/api';

  const response = await fetch(
    `${API_BASE_URL}/typed-proformas/lcl/${id}/pdf`,
    {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) {
    const message = await response.text();

    throw new Error(
      message ||
      `No se pudo descargar PDF (${response.status})`
    );
  }

  const contentType =
    response.headers.get('content-type') || '';

  if (!contentType.includes('application/pdf')) {
    throw new Error(
      `Respuesta inválida al descargar PDF: ${contentType}`
    );
  }

  return await response.blob();
}