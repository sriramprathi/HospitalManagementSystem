import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("hmsUser"));
    } catch (e) {
      return null;
    }
  });

  const signIn = (data) => {
    localStorage.setItem("hmsUser", JSON.stringify(data));
    setUser(data);
  };

  const signOut = () => {
    localStorage.removeItem("hmsUser");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

// shortcut so pages write useAuth() instead of useContext(AuthContext)
export const useAuth = () => useContext(AuthContext);