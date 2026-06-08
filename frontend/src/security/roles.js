export function hasRole(roles = [], role) {
  return roles.includes(role);
}

export function hasAnyRole(roles = [], allowedRoles = []) {
  return allowedRoles.some((role) => roles.includes(role));
}

export function canCreateProforma(roles = []) {
  return hasAnyRole(roles, ['VENDEDOR', 'ADMIN', 'OWNER']);
}

export function canApproveProforma(roles = []) {
  return hasAnyRole(roles, [
    'SUPERVISOR',
    'JEFE_COMERCIAL',
    'GERENCIA',
    'ADMIN',
    'OWNER',
  ]);
}

export function canManageParameters(roles = []) {
  return hasAnyRole(roles, ['ADMIN', 'OWNER']);
}