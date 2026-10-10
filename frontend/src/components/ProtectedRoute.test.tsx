import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { describe, expect, it } from "vitest";
import { AuthContext, type AuthContextValue } from "../auth/AuthContext";
import ProtectedRoute from "./ProtectedRoute";

function renderAt(isAuthenticated: boolean) {
  const value: AuthContextValue = {
    isAuthenticated,
    login: async () => {},
    register: async () => {},
    logout: () => {},
  };

  return render(
    <AuthContext.Provider value={value}>
      <MemoryRouter initialEntries={["/notifications"]}>
        <Routes>
          <Route path="/login" element={<p>pantalla de login</p>} />
          <Route element={<ProtectedRoute />}>
            <Route path="/notifications" element={<p>contenido privado</p>} />
          </Route>
        </Routes>
      </MemoryRouter>
    </AuthContext.Provider>
  );
}

describe("ProtectedRoute", () => {
  it("sin sesion redirige al login", () => {
    renderAt(false);
    expect(screen.getByText("pantalla de login")).toBeInTheDocument();
    expect(screen.queryByText("contenido privado")).not.toBeInTheDocument();
  });

  it("con sesion muestra el contenido", () => {
    renderAt(true);
    expect(screen.getByText("contenido privado")).toBeInTheDocument();
  });
});