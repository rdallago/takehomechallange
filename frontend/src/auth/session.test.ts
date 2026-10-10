import { beforeEach, describe, expect, it } from "vitest";
import { clearToken, getToken, setToken } from "./session";

describe("session", () => {
  beforeEach(() => localStorage.clear());

  it("devuelve null si no hay token guardado", () => {
    expect(getToken()).toBeNull();
  });

  it("guarda y recupera el token", () => {
    setToken("abc123");
    expect(getToken()).toBe("abc123");
  });

  it("clearToken elimina el token", () => {
    setToken("abc123");
    clearToken();
    expect(getToken()).toBeNull();
  });
});