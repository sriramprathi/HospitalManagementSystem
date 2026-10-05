import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { canAccess, PATHS } from "../config/roles";

export default function ProtectedRoute({ children }) {
  const { user } = useAuth();
  const location = useLocation();

  if (!user) return <Navigate to="/login" replace />;
  if (!canAccess(user.role, location.pathname)) return <Navigate to={PATHS.dashboard} replace />;
  return children;
}
