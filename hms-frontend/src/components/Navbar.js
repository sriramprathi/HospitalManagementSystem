import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { PATHS } from "../config/roles";
import { roleLabel } from "../config/constants";

export default function Navbar({ onToggleSidebar }) {
  const { user, signOut } = useAuth();
  const canOpenProfile = user.role === "DOCTOR" || user.role === "PATIENT";

  return (
    <nav className="topbar navbar sticky-top px-3">
      <div className="d-flex align-items-center">
        <button
          type="button"
          className="btn btn-link text-dark d-md-none me-2 p-0 px-1"
          onClick={onToggleSidebar}
          aria-label="Toggle menu"
        >
          <i className="bi bi-list fs-3"></i>
        </button>
        <span className="brand">
          <span className="brand-mark"><i className="bi bi-hospital"></i></span>
          HMS
        </span>
      </div>

      <div className="d-flex align-items-center gap-2">
        {canOpenProfile && (
          <Link to={PATHS.profile} className="btn btn-outline-primary btn-sm">
            <i className="bi bi-person-circle me-1"></i>Profile
          </Link>
        )}
        <span className="avatar">{roleLabel(user.role)?.charAt(0)}</span>
        <div className="lh-sm d-none d-sm-block me-2">
          <div className="fw-semibold small">{roleLabel(user.role)}</div>
          <div className="text-muted" style={{ fontSize: "0.75rem" }}>User #{user.userId}</div>
        </div>
        <button type="button" className="btn btn-outline-secondary btn-sm" onClick={signOut}>
          <i className="bi bi-box-arrow-right me-1"></i>Logout
        </button>
      </div>
    </nav>
  );
}
