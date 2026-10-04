import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { canAccess, PATHS } from "../config/roles";

export default function ProtectedRoute({ children }) {
  const { user } = useAuth();
  const location = useLocation();

  // 1. not logged in -> go to the login page
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // 2. logged in, but this role may not open this page -> go to the dashboard
  if (!canAccess(user.role, location.pathname)) {
    return <Navigate to={PATHS.dashboard} replace />;
  }

  // 3. allowed -> show the page
  return children;
}