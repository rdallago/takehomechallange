import { clearToken, getToken } from "../auth/session";
import type {
  NotificationItem,
  NotificationRequest,
  TokenResponse,
  UserResponse,
} from "./types";

const BASE_URL: string = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

// Error de la API con el status HTTP y los mensajes que devuelve el backend
export class ApiError extends Error {
  status: number;
  details: string[];

  constructor(status: number, message: string, details: string[] = []) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.details = details;
  }
}

// La sesion registra aca que hacer cuando el token deja de servir
let onUnauthorized: (() => void) | null = null;

export function setUnauthorizedHandler(handler: (() => void) | null): void {
  onUnauthorized = handler;
}

async function toApiError(response: Response): Promise<ApiError> {
  try {
    // Formato del backend: { status, error, message, details }
    const body = await response.json();
    return new ApiError(
      response.status,
      body.message ?? "Error inesperado",
      Array.isArray(body.details) ? body.details : []
    );
  } catch {
    // La respuesta no era JSON (por ejemplo un 502 del proxy de Render)
    return new ApiError(response.status, `Error ${response.status} del servidor`);
  }
}

async function request<T>(
  path: string,
  options: { method?: string; body?: unknown } = {}
): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {};
  if (options.body !== undefined) headers["Content-Type"] = "application/json";
  if (token) headers["Authorization"] = `Bearer ${token}`;

  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      method: options.method ?? "GET",
      headers,
      body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    });
  } catch {
    // fetch solo falla por red, CORS o servidor caido
    throw new ApiError(
      0,
      "No se pudo conectar con el servidor. Si esta en Render, puede estar despertando: reintentar en unos segundos."
    );
  }

  // 401 con token enviado = token vencido o invalido: se cierra la sesion.
  // Sin token (por ejemplo un login con clave mala) no se toca la sesion.
  if (response.status === 401 && token) {
    clearToken();
    onUnauthorized?.();
  }

  if (!response.ok) throw await toApiError(response);

  if (response.status === 204) return undefined as T; // DELETE no devuelve cuerpo
  return (await response.json()) as T;
}

export const authApi = {
  register: (email: string, password: string) =>
    request<UserResponse>("/api/auth/register", { method: "POST", body: { email, password } }),

  login: (email: string, password: string) =>
    request<TokenResponse>("/api/auth/login", { method: "POST", body: { email, password } }),
};

export const notificationsApi = {
  list: () => request<NotificationItem[]>("/api/notifications"),

  create: (data: NotificationRequest) =>
    request<NotificationItem>("/api/notifications", { method: "POST", body: data }),

  update: (id: string, data: NotificationRequest) =>
    request<NotificationItem>(`/api/notifications/${id}`, { method: "PUT", body: data }),

  remove: (id: string) =>
    request<void>(`/api/notifications/${id}`, { method: "DELETE" }),
};