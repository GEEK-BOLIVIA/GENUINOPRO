import { createContext, useContext, useEffect, useState } from 'react';
import keycloak from './keycloak';
import { setApiToken } from '../api/apiClient';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [ready, setReady] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);
  const [token, setToken] = useState(null);

  useEffect(() => {
    keycloak
      .init({
        onLoad: 'login-required',
        checkLoginIframe: false,
      })
      .then((auth) => {
        const currentToken = keycloak.token || null;

        setAuthenticated(auth);
        setToken(currentToken);
        setApiToken(currentToken);

        setReady(true);
      })
      .catch((error) => {
        console.error('Keycloak init error', error);
        setReady(true);
      });
  }, []);

  async function refreshToken() {
    await keycloak.updateToken(30);

    const refreshedToken = keycloak.token || null;

    setToken(refreshedToken);
    setApiToken(refreshedToken);

    return refreshedToken;
  }

  if (!ready) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-950 text-sm font-bold text-slate-300">
        Cargando sesión Genuino CRM...
      </div>
    );
  }

  return (
    <AuthContext.Provider
      value={{
        authenticated,
        token,
        username: keycloak.tokenParsed?.preferred_username || 'Usuario',
        tokenParsed: keycloak.tokenParsed,
        roles: keycloak.tokenParsed?.realm_access?.roles || [],
        logout: () => keycloak.logout(),
        refreshToken,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}