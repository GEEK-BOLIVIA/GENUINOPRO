export {
  apiFetch,
  setApiToken,
  getApiToken,
} from '../api/apiClient';

export async function downloadLclPdf(id, token) {
  const response = await fetch(
    `http://localhost:8081/api/typed-proformas/lcl/${id}/pdf`,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) {
    throw new Error('No se pudo descargar PDF');
  }

  return await response.blob();
}