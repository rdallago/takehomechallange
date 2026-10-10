import { useState, type FormEvent } from "react";
import { errorMessage } from "../api/errors";
import type { Channel, NotificationItem, NotificationRequest } from "../api/types";

const SMS_MAX = 160;

// Ayuda segun el canal: el destinatario es un email, un telefono o un token
const RECIPIENT_HINTS: Record<Channel, { label: string; placeholder: string }> = {
  EMAIL: { label: "Email del destinatario", placeholder: "alguien@mail.com" },
  SMS: { label: "Telefono (con codigo de pais)", placeholder: "+5493794123456" },
  PUSH: { label: "Token del dispositivo (minimo 20 caracteres)", placeholder: "abcdefghij1234567890xyz" },
};

interface Props {
  initial: NotificationItem | null; // null = alta, con datos = edicion
  onSubmit: (data: NotificationRequest) => Promise<void>;
  onCancel: () => void;
}

export default function NotificationForm({ initial, onSubmit, onCancel }: Props) {
  const [title, setTitle] = useState(initial?.title ?? "");
  const [content, setContent] = useState(initial?.content ?? "");
  const [channel, setChannel] = useState<Channel>(initial?.channel ?? "EMAIL");
  const [recipient, setRecipient] = useState(initial?.recipient ?? "");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  const smsTooLong = channel === "SMS" && content.length > SMS_MAX;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError("");
    setSaving(true);
    try {
      await onSubmit({
        title: title.trim(),
        content,
        channel,
        recipient: recipient.trim(),
      });
      // Si era un alta, se limpia el formulario. En edicion, la pagina cambia
      // de modo y este componente se vuelve a crear solo (por la prop key)
      if (!initial) {
        setTitle("");
        setContent("");
        setRecipient("");
      }
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setSaving(false);
    }
  }

  const hint = RECIPIENT_HINTS[channel];

  return (
    <form onSubmit={handleSubmit} className="notification-form">
      <h2>{initial ? "Editar notificacion" : "Nueva notificacion"}</h2>
      {error && <p className="error" role="alert">{error}</p>}

      <label>
        Canal
        <select value={channel} onChange={(e) => setChannel(e.target.value as Channel)}>
          <option value="EMAIL">Email</option>
          <option value="SMS">SMS</option>
          <option value="PUSH">Push</option>
        </select>
      </label>

      <label>
        Titulo
        <input
          required
          maxLength={150}
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />
      </label>

      <label>
        Contenido
        <textarea
          required
          rows={3}
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        {channel === "SMS" && (
          <small className={smsTooLong ? "counter over" : "counter"}>
            {content.length} / {SMS_MAX} caracteres
          </small>
        )}
      </label>

      <label>
        {hint.label}
        <input
          required
          maxLength={255}
          placeholder={hint.placeholder}
          value={recipient}
          onChange={(e) => setRecipient(e.target.value)}
        />
      </label>

      <div className="form-actions">
        <button type="submit" disabled={saving || smsTooLong}>
          {saving ? "Guardando..." : initial ? "Guardar cambios" : "Crear y enviar"}
        </button>
        {initial && (
          <button type="button" className="secondary" onClick={onCancel}>
            Cancelar
          </button>
        )}
      </div>
      {initial && <small>Editar no vuelve a enviar la notificacion.</small>}
    </form>
  );
}