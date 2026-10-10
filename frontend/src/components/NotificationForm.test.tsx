import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { ApiError } from "../api/client";
import type { NotificationItem } from "../api/types";
import NotificationForm from "./NotificationForm";

describe("NotificationForm", () => {
  it("en SMS muestra el contador y bloquea el envio sobre 160 caracteres", async () => {
    const user = userEvent.setup();
    render(<NotificationForm initial={null} onSubmit={vi.fn()} onCancel={vi.fn()} />);

    await user.selectOptions(screen.getByLabelText(/^Canal/), "SMS");
    await user.click(screen.getByLabelText(/^Contenido/));
    await user.paste("a".repeat(161));

    expect(screen.getByText("161 / 160 caracteres")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Crear y enviar" })).toBeDisabled();
  });

  it("cambia la ayuda del destinatario segun el canal", async () => {
    const user = userEvent.setup();
    render(<NotificationForm initial={null} onSubmit={vi.fn()} onCancel={vi.fn()} />);

    expect(screen.getByLabelText(/Email del destinatario/)).toBeInTheDocument();

    await user.selectOptions(screen.getByLabelText(/^Canal/), "PUSH");

    expect(screen.getByLabelText(/Token del dispositivo/)).toBeInTheDocument();
  });

  it("envia los datos sin espacios sobrantes y limpia el formulario", async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    render(<NotificationForm initial={null} onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByLabelText(/^Titulo/), "  Hola  ");
    await user.type(screen.getByLabelText(/^Contenido/), "Mensaje");
    await user.type(screen.getByLabelText(/destinatario/i), " a@mail.com ");
    await user.click(screen.getByRole("button", { name: "Crear y enviar" }));

    expect(onSubmit).toHaveBeenCalledWith({
      title: "Hola",
      content: "Mensaje",
      channel: "EMAIL",
      recipient: "a@mail.com",
    });
    await waitFor(() => expect(screen.getByLabelText(/^Titulo/)).toHaveValue(""));
  });

  it("muestra el error que devuelve el backend", async () => {
    const user = userEvent.setup();
    const onSubmit = vi
      .fn()
      .mockRejectedValue(new ApiError(400, "Datos de entrada invalidos", ["recipient: no valido"]));
    render(<NotificationForm initial={null} onSubmit={onSubmit} onCancel={vi.fn()} />);

    await user.type(screen.getByLabelText(/^Titulo/), "Hola");
    await user.type(screen.getByLabelText(/^Contenido/), "Mensaje");
    await user.type(screen.getByLabelText(/destinatario/i), "x@mail.com");
    await user.click(screen.getByRole("button", { name: "Crear y enviar" }));

    expect(await screen.findByRole("alert")).toHaveTextContent(/Datos de entrada invalidos/);
  });

  it("en modo edicion precarga los datos y ofrece cancelar", () => {
    const item: NotificationItem = {
      id: "1",
      title: "Viejo",
      content: "Contenido",
      channel: "SMS",
      recipient: "+5493794123456",
      createdAt: "2026-10-01T00:00:00Z",
      updatedAt: "2026-10-01T00:00:00Z",
    };
    render(<NotificationForm initial={item} onSubmit={vi.fn()} onCancel={vi.fn()} />);

    expect(screen.getByLabelText(/^Titulo/)).toHaveValue("Viejo");
    expect(screen.getByRole("button", { name: "Guardar cambios" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Cancelar" })).toBeInTheDocument();
    expect(screen.getByText("Editar no vuelve a enviar la notificacion.")).toBeInTheDocument();
  });
});