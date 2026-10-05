import { NavLink } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ROLES } from "../config/roles";

export default function Sidebar({ open, onNavigate }) {
  const { user } = useAuth();
  const menu = ROLES[user.role]?.menu || [];

  return (
    <aside className={`sidebar ${open ? "d-block" : "d-none"} d-md-block`}>
      <nav>
        {menu.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            onClick={onNavigate}
            className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
          >
            <i className={`bi ${item.icon}`}></i>
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
