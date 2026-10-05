import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  // user = { userId, role, profileId }  (profileId = patientId or doctorId)
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

  return <AuthContext.Provider value={{ user, signIn, signOut }}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
