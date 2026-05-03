CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  price DOUBLE PRECISION NOT NULL
);

INSERT INTO users (email, password)
VALUES ('demo@local.test', 'password123')
ON CONFLICT (email) DO NOTHING;

INSERT INTO products (name, price)
VALUES
  ('Starter Plan', 19.0),
  ('Team Plan', 49.0),
  ('Enterprise Plan', 129.0);
