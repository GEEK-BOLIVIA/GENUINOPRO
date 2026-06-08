import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'genuino',
  clientId: 'genuino-crm-frontend',
});

export default keycloak;