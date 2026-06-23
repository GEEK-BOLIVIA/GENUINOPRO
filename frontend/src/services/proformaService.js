import {
  getLclProformas,
  getLclProformaById,
  approveLclProforma,
  rejectLclProforma,
  submitLclForReview,
  clientAcceptLclProforma,
  clientRejectLclProforma,
} from './lclService';

import { getFclProformas } from './fclService';

const SERVICES = {
  LCL: {
    list: getLclProformas,
    detail: getLclProformaById,
    approve: approveLclProforma,
    reject: rejectLclProforma,
    submitReview: submitLclForReview,
    clientAccept: clientAcceptLclProforma,
    clientReject: clientRejectLclProforma,
  },
  FCL: {
    list: getFclProformas,
  },
};

function getService(type) {
  const normalizedType = String(type || '').toUpperCase();
  const service = SERVICES[normalizedType];

  if (!service) {
    throw new Error(`El módulo ${normalizedType} todavía no está implementado.`);
  }

  return service;
}

export async function getProformas(type) {
  return getService(type).list();
}

export async function getProformaById(type, id) {
  return getService(type).detail(id);
}

export async function approveProforma(type, id) {
  return getService(type).approve(id);
}

export async function rejectProforma(type, id) {
  return getService(type).reject(id);
}

export async function sendProformaToReview(type, id) {
  return getService(type).submitReview(id);
}

export async function clientAcceptProforma(type, id) {
  return getService(type).clientAccept(id);
}

export async function clientRejectProforma(type, id, reason) {
  return getService(type).clientReject(id, reason);
}