import { useCallback, useEffect, useMemo, useState, type ReactNode } from "react";
import { authApi, setUnauthorizedHandler } from "../api/client";
import { AuthContext, type AuthContextValue } from "./AuthContext";
import { clearToken, getToken, setToken } from "./session";

export function AuthProvider({ children }: { children: ReactNode }) {
  // Al abrir la app se recupera la sesion si habia un token guardado
  const [token, setTokenState] = useState<string | null>(getToken());

  const logout = useCallback(() => {
    clearToken();
    setTokenState(null);
  }, []);

  // Si la API responde 401 con un token, el cliente avisa y se cierra la sesion
  useEffect(() => {
    setUnauthorizedHandler(() => setTokenState(null));
    return () => setUnauthorizedHandler(null);
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    const response = await authApi.login(email, password);
    setToken(response.accessToken);
    setTokenState(response.accessToken);
  }, []);

  // Registrarse no inicia sesion: el backend solo devuelve el usuario creado
  const register = useCallback(async (email: string, password: string) => {
    await authApi.register(email, password);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({ isAuthenticated: token !== null, login, register, logout }),
    [token, login, register, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}