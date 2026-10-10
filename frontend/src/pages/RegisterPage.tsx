import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { errorMessage } from "../api/errors";
import { useAuth } from "../auth/AuthContext";

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await register(email, password);
      navigate("/login", { state: { registered: true } });
    } catch (err) {
      setError(errorMessage(err)); // por ejemplo el 409 de email repetido
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="card auth-card">
      <h1>Crear cuenta</h1>
      {error && <p className="error" role="alert">{error}</p>}
      <form onSubmit={handleSubmit}>
        <label>
          Email
          <input
            type="email"
            required
            maxLength={255}
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </label>
        <label>
          Contraseña (8 a 72 caracteres)
          <input
            type="password"
            required
            minLength={8}
            maxLength={72}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>
        <button type="submit" disabled={loading}>
          {loading ? "Creando..." : "Registrarme"}
        </button>
      </form>
      <p>
        ¿Ya tenes cuenta? <Link to="/login">Inicia sesion</Link>
      </p>
    </main>
  );
}