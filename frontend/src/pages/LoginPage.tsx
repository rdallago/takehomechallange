import { useState, type FormEvent } from "react";
import { Link, useLocation } from "react-router-dom";
import { errorMessage } from "../api/errors";
import { useAuth } from "../auth/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const location = useLocation();
  // El registro nos manda aca con un aviso de exito
  const registered = (location.state as { registered?: boolean } | null)?.registered;

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(email, password);
      // No hace falta navegar: PublicOnlyRoute redirige al detectar la sesion
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="card auth-card">
      <h1>Iniciar sesion</h1>
      {registered && <p className="success">Usuario creado. Ya podes iniciar sesion.</p>}
      {error && <p className="error" role="alert">{error}</p>}
      <form onSubmit={handleSubmit}>
        <label>
          Email
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </label>
        <label>
          Contraseña
          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        <button type="submit" disabled={loading}>
          {loading ? "Ingresando..." : "Ingresar"}
        </button>
      </form>
      <p>
        ¿No tenes cuenta? <Link to="/register">Registrate</Link>
      </p>
    </main>
  );
}