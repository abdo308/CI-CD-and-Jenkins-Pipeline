import { useEffect, useState } from "react";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8082";
const AUTH_URL = import.meta.env.VITE_AUTH_URL || "http://localhost:8081";

export default function App() {
  const [email, setEmail] = useState("demo@local.test");
  const [password, setPassword] = useState("password123");
  const [token, setToken] = useState(() => localStorage.getItem("token") || "");
  const [status, setStatus] = useState("");
  const [products, setProducts] = useState([]);
  const [userEmail, setUserEmail] = useState("");

  useEffect(() => {
    if (!token) {
      return;
    }

    loadProducts(token);
  }, [token]);

  const handleLogin = async (event) => {
    event.preventDefault();
    setStatus("Signing in...");

    try {
      const response = await fetch(`${AUTH_URL}/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
      });

      if (!response.ok) {
        setStatus("Login failed. Check credentials.");
        return;
      }

      const data = await response.json();
      if (!data.token) {
        setStatus("Login failed. No token received.");
        return;
      }

      localStorage.setItem("token", data.token);
      setToken(data.token);
      setStatus("");
    } catch (error) {
      setStatus("Login failed. Try again.");
    }
  };

  const loadProducts = async (activeToken) => {
    setStatus("Loading products...");

    try {
      const response = await fetch(`${API_URL}/products`, {
        headers: {
          Authorization: `Bearer ${activeToken}`
        }
      });

      if (!response.ok) {
        setStatus("Session expired. Please sign in again.");
        setToken("");
        localStorage.removeItem("token");
        setProducts([]);
        setUserEmail("");
        return;
      }

      const data = await response.json();
      setProducts(Array.isArray(data.items) ? data.items : []);
      setUserEmail(data.user || "");
      setStatus("");
    } catch (error) {
      setStatus("Could not load products.");
    }
  };

  const logout = () => {
    setToken("");
    localStorage.removeItem("token");
    setProducts([]);
    setUserEmail("");
    setStatus("");
  };

  return (
    <div className="page">
      <div className="orb orb-one" />
      <div className="orb orb-two" />
      <div className="orb orb-three" />

      <header className="hero">
        <div>
          <p className="kicker">Microservices Control Desk</p>
          <h1>Java Services + React Frontend</h1>
          <p className="subtitle">
            Auth, API, and database pipelines wired together locally with Docker Compose.
          </p>
        </div>
        <div className="meta">
          <div>
            <span>Auth</span>
            <strong>{AUTH_URL}</strong>
          </div>
          <div>
            <span>API</span>
            <strong>{API_URL}</strong>
          </div>
        </div>
      </header>

      <main className="grid">
        <section className="card">
          <h2>Access</h2>
          <p className="muted">Use the demo credentials or replace them in the database.</p>
          <form onSubmit={handleLogin} className="form">
            <label>
              Email
              <input
                type="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                required
              />
            </label>
            <label>
              Password
              <input
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
              />
            </label>
            <div className="actions">
              <button type="submit">Sign In</button>
              {token && (
                <button type="button" className="ghost" onClick={logout}>
                  Sign Out
                </button>
              )}
            </div>
          </form>
          {status && <p className="status">{status}</p>}
          {userEmail && <p className="status">Signed in as {userEmail}</p>}
        </section>

        <section className="card">
          <h2>Products</h2>
          <p className="muted">Protected by the auth service. Requires a valid token.</p>
          <div className="list">
            {products.length === 0 ? (
              <div className="empty">No products loaded yet.</div>
            ) : (
              products.map((product) => (
                <div className="list-item" key={product.id}>
                  <div>
                    <strong>{product.name}</strong>
                    <span>Plan ID {product.id}</span>
                  </div>
                  <div className="price">${product.price.toFixed(2)}</div>
                </div>
              ))
            )}
          </div>
        </section>

        <section className="card wide">
          <h2>System Signals</h2>
          <div className="signals">
            <div>
              <span>Auth Health</span>
              <strong>GET /health</strong>
            </div>
            <div>
              <span>API Health</span>
              <strong>GET /health</strong>
            </div>
            <div>
              <span>Products</span>
              <strong>GET /products</strong>
            </div>
            <div>
              <span>Login</span>
              <strong>POST /login</strong>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
}
