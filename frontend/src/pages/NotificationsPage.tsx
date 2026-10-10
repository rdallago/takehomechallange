import { useEffect, useState } from "react";
import { notificationsApi } from "../api/client";
import { errorMessage } from "../api/errors";
import type { Channel, NotificationItem, NotificationRequest } from "../api/types";
import { useAuth } from "../auth/AuthContext";
import NotificationForm from "../components/NotificationForm";

const CHANNEL_LABELS: Record<Channel, string> = {
  EMAIL: "Email",
  SMS: "SMS",
  PUSH: "Push",
};

export default function NotificationsPage() {
  const { logout } = useAuth();
  const [items, setItems] = useState<NotificationItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  // Notificacion que se esta editando (null = el formulario esta en modo alta)
  const [editing, setEditing] = useState<NotificationItem | null>(null);

  // Carga inicial de la lista
  useEffect(() => {
    let cancelled = false; // evita actualizar el estado si el componente ya se desmonto
    notificationsApi
      .list()
      .then((data) => {
        if (!cancelled) setItems(data);
      })
      .catch((e) => {
        if (!cancelled) setError(errorMessage(e));
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  // Alta o edicion, segun el modo. Si falla, el error se muestra en el formulario
  async function handleSubmit(data: NotificationRequest) {
    if (editing) {
      const updated = await notificationsApi.update(editing.id, data);
      setItems((prev) => prev.map((n) => (n.id === updated.id ? updated : n)));
      setEditing(null);
    } else {
      const created = await notificationsApi.create(data);
      setItems((prev) => [created, ...prev]); // la lista viene ordenada de mas nueva a mas vieja
    }
  }

  async function handleDelete(item: NotificationItem) {
    if (!window.confirm(`¿Eliminar "${item.title}"?`)) return;
    setError("");
    try {
      await notificationsApi.remove(item.id);
      setItems((prev) => prev.filter((n) => n.id !== item.id));
      if (editing?.id === item.id) setEditing(null);
    } catch (e) {
      setError(errorMessage(e));
    }
  }

  return (
    <main className="card">
      <header className="page-header">
        <h1>Mis notificaciones</h1>
        <button className="secondary" onClick={logout}>Cerrar sesion</button>
      </header>

      {/* La key hace que el formulario se vuelva a crear al cambiar de notificacion */}
      <NotificationForm
        key={editing?.id ?? "nueva"}
        initial={editing}
        onSubmit={handleSubmit}
        onCancel={() => setEditing(null)}
      />

      {error && <p className="error" role="alert">{error}</p>}
      {loading && <p>Cargando...</p>}
      {!loading && items.length === 0 && !error && (
        <p>Todavia no tenes notificaciones. Crea la primera con el formulario.</p>
      )}

      <ul className="notification-list">
        {items.map((n) => (
          <li key={n.id} className="notification-item">
            <div>
              <span className={`badge badge-${n.channel.toLowerCase()}`}>
                {CHANNEL_LABELS[n.channel]}
              </span>
              <strong> {n.title}</strong>
              <p>{n.content}</p>
              <small>
                Para: {n.recipient} · {new Date(n.createdAt).toLocaleString()}
              </small>
            </div>
            <div className="item-actions">
              <button className="secondary" onClick={() => setEditing(n)}>Editar</button>
              <button className="danger" onClick={() => handleDelete(n)}>Eliminar</button>
            </div>
          </li>
        ))}
      </ul>
    </main>
  );
}