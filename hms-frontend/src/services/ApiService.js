const BASE_URL = "http://localhost:8080/api";

// Every backend call goes through here
export async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });

  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const body = await res.json();
      message = body.message || message;
    } catch (e) {
      // response had no JSON body
    }
    throw new Error(message);
  }

  return res.status === 204 ? null : res.json();
}

export const login = (userName, password) =>
  request("/users/login", {
    method: "POST",
    body: JSON.stringify({ userName, password }),
  });