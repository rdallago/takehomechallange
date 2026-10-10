import { beforeEach, afterEach, describe, expect, it, vi } from "vitest";
import { getToken, setToken } from "../auth/session";
import { ApiError, authApi, notificationsApi, setUnauthorizedHandler } from "./client";

function jsonResponse(status: number, body: unknown): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}

describe("cliente de API", () => {
  const fetchMock = vi.fn();

  beforeEach(() => {
    localStorage.clear();
    setUnauthorizedHandler(null);
    fetchMock.mockReset();
    vi.stubGlobal("fetch", fetchMock);
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("adjunta el token en el header Authorization", async () => {
    setToken("abc");
    fetchMock.mockResolvedValue(jsonResponse(200, []));

    await notificationsApi.list();

    const init = fetchMock.mock.calls[0][1] as RequestInit;
    const headers = init.headers as Record<string, string>;
    expect(headers["Authorization"]).toBe("Bearer abc");
  });

  it("no manda Authorization si no hay token", async () => {
    fetchMock.mockResolvedValue(jsonResponse(200, { accessToken: "t", tokenType: "Bearer" }));

    await authApi.login("a@mail.com", "clave12345");

    const init = fetchMock.mock.calls[0][1] as RequestInit;
    const headers = init.headers as Record<string, string>;
    expect(headers["Authorization"]).toBeUndefined();
  });

  it("ante un 401 con token cierra la sesion y avisa", async () => {
    setToken("vencido");
    const handler = vi.fn();
    setUnauthorizedHandler(handler);
    fetchMock.mockResolvedValue(jsonResponse(401, { message: "Token ausente o invalido" }));

    await expect(notificationsApi.list()).rejects.toMatchObject({ status: 401 });

    expect(getToken()).toBeNull();
    expect(handler).toHaveBeenCalledOnce();
  });

  it("ante un 401 sin token (login fallido) no toca la sesion", async () => {
    const handler = vi.fn();
    setUnauthorizedHandler(handler);
    fetchMock.mockResolvedValue(jsonResponse(401, { message: "Email o contraseña incorrectos" }));

    await expect(authApi.login("a@mail.com", "mala")).rejects.toMatchObject({
      status: 401,
      message: "Email o contraseña incorrectos",
    });

    expect(handler).not.toHaveBeenCalled();
  });

  it("traduce el cuerpo de error del backend, incluidos los detalles", async () => {
    fetchMock.mockResolvedValue(
      jsonResponse(400, { message: "Datos de entrada invalidos", details: ["title: no debe estar vacio"] })
    );

    const error = await notificationsApi
      .create({ title: "", content: "x", channel: "EMAIL", recipient: "a@mail.com" })
      .catch((e) => e);

    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(400);
    expect(error.details).toEqual(["title: no debe estar vacio"]);
  });

  it("ante un fallo de red devuelve un ApiError con status 0", async () => {
    fetchMock.mockRejectedValue(new TypeError("Failed to fetch"));

    await expect(notificationsApi.list()).rejects.toMatchObject({ status: 0 });
  });

  it("devuelve undefined ante un 204 sin cuerpo (DELETE)", async () => {
    fetchMock.mockResolvedValue(new Response(null, { status: 204 }));

    await expect(notificationsApi.remove("id-1")).resolves.toBeUndefined();
  });

  it("maneja errores que no son JSON (por ejemplo un 502 del proxy)", async () => {
    fetchMock.mockResolvedValue(new Response("Bad Gateway", { status: 502 }));

    await expect(notificationsApi.list()).rejects.toMatchObject({
      status: 502,
      message: expect.stringContaining("502"),
    });
  });
});