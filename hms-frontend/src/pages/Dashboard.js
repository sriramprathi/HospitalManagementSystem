import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import { canAccess, PATHS, ROLES } from "../config/roles";
import { todayStr } from "../config/constants";
import {
  getAllDoctors,
  getAllPatients,
  getAppointmentsByDate,
  getAppointmentsByDoctor,
  getAppointmentsByPatient,
} from "../services/ApiService";

// returns a list of stat cards for the logged-in role
function loadStats(user) {
  const { role, profileId } = user;

  if (role === "Administrator" || role === "Receptionist") {
    return Promise.all([getAllPatients(), getAllDoctors(), getAppointmentsByDate(todayStr())]).then(([p, d, a]) => [
      { label: "Patients", value: p.length, icon: "bi-people", color: "primary", path: PATHS.patients },
      { label: "Doctors", value: d.length, icon: "bi-person-badge", color: "success", path: PATHS.doctors },
      { label: "Today's appointments", value: a.length, icon: "bi-calendar-check", color: "warning", path: PATHS.appointments },
    ]);
  }

  if (role === "Doctor" && profileId) {
    return getAppointmentsByDoctor(profileId).then((list) => [
      { label: "All appointments", value: list.length, icon: "bi-calendar3", color: "primary", path: PATHS.appointments },
      {
        label: "Scheduled today",
        value: list.filter((a) => a.date === todayStr() && a.status === "Scheduled").length,
        icon: "bi-calendar-day",
        color: "warning",
        path: PATHS.appointments,
      },
      {
        label: "Completed",
        value: list.filter((a) => a.status === "Completed").length,
        icon: "bi-check2-circle",
        color: "success",
        path: PATHS.appointments,
      },
    ]);
  }

  if (role === "Patient" && profileId) {
    return getAppointmentsByPatient(profileId).then((list) => [
      { label: "All appointments", value: list.length, icon: "bi-calendar3", color: "primary", path: PATHS.appointments },
      {
        label: "Upcoming",
        value: list.filter((a) => a.status === "Scheduled").length,
        icon: "bi-calendar-day",
        color: "warning",
        path: PATHS.appointments,
      },
    ]);
  }

  return Promise.resolve([]);
}

export default function Dashboard() {
  const { user } = useAuth();
  const { data: stats, loading, error } = useApi(() => loadStats(user), [user.role, user.profileId]);
  const links = (ROLES[user.role]?.menu || []).filter((m) => m.path !== PATHS.dashboard);
  const missingProfile = (user.role === "Patient" || user.role === "Doctor") && !user.profileId;
  const longDate = new Date().toLocaleDateString(undefined, {
    weekday: "long", day: "numeric", month: "long", year: "numeric",
  });

  return (
    <div>
      <div className="welcome-panel d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1">Welcome back, {user.role}</h4>
          <span className="opacity-75">{longDate}</span>
        </div>
        <i className="bi bi-heart-pulse welcome-icon d-none d-sm-block"></i>
      </div>

      {missingProfile && (
        <div className="alert alert-warning">
          No {user.role.toLowerCase()} profile is linked to this login, so personal data cannot be shown.
        </div>
      )}
      {error && <div className="alert alert-danger">{error}</div>}
      {loading && <Loader />}

      {!loading && stats && stats.length > 0 && (
        <div className="row g-3 mb-4">
          {stats.map((s) => {
            const inner = (
              <div className="card-body d-flex align-items-center gap-3">
                <span className={`stat-icon ${s.color}`}><i className={`bi ${s.icon}`}></i></span>
                <div>
                  <div className="fs-3 fw-bold lh-1">{s.value}</div>
                  <div className="text-muted small mt-1">{s.label}</div>
                </div>
              </div>
            );
            return (
              <div className="col-12 col-md-6 col-xl-4" key={s.label}>
                {canAccess(user.role, s.path) ? (
                  <Link to={s.path} className="stat-card card border-0 h-100">{inner}</Link>
                ) : (
                  <div className="stat-card card border-0 h-100">{inner}</div>
                )}
              </div>
            );
          })}
        </div>
      )}

      <h6 className="fw-semibold mb-3">Quick links</h6>
      <div className="row g-3">
        {links.map((l) => (
          <div className="col-6 col-md-4 col-xl-3" key={l.path}>
            <div className="card border-0 h-100">
              <Link to={l.path} className="quick-link">
                <i className={`bi ${l.icon}`}></i>
                <span className="fw-medium">{l.label}</span>
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
