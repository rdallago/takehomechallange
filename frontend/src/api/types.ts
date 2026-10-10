export type Channel = "EMAIL" | "SMS" | "PUSH";

// Se llama NotificationItem porque "Notification" ya existe en el navegador
// (es la API de notificaciones del sistema) y se prestaria a confusion
export interface NotificationItem {
  id: string;
  title: string;
  content: string;
  channel: Channel;
  recipient: string;
  createdAt: string;
  updatedAt: string;
}

// Sirve para crear y para modificar, igual que en el backend
export interface NotificationRequest {
  title: string;
  content: string;
  channel: Channel;
  recipient: string;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
}

export interface UserResponse {
  id: string;
  email: string;
  createdAt: string;
}