import { useAuth } from "../context/AuthContext";

export default function Navbar({ onToggleSidebar }) {
  const { user, signOut } = useAuth();

  return (
    <nav className="topbar navbar sticky-top px-3">
      <div className="d-flex align-items-center">
        <button
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
        <span className="avatar">{user.role?.charAt(0)}</span>
        <div className="lh-sm d-none d-sm-block me-2">
          <div className="fw-semibold small">{user.role}</div>
          <div className="text-muted" style={{ fontSize: "0.75rem" }}>User #{user.userId}</div>
        </div>
        <button className="btn btn-outline-secondary btn-sm" onClick={signOut}>
          <i className="bi bi-box-arrow-right me-1"></i>Logout
        </button>
      </div>
    </nav>
  );
}
