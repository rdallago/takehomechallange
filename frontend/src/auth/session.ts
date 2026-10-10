const TOKEN_KEY = "accessToken";

// Todo va en try/catch porque localStorage puede estar bloqueado
// (modo privado, politicas del navegador)
export function getToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY);
  } catch {
    return null;
  }
}

export function setToken(token: string): void {
  try {
    localStorage.setItem(TOKEN_KEY, token);
  } catch {
    // sin storage la sesion no persiste, pero la app sigue andando
  }
}

export function clearToken(): void {
  try {
    localStorage.removeItem(TOKEN_KEY);
  } catch {
    // nada que hacer
  }
}