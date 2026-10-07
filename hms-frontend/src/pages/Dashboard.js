import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import { canAccess, PATHS } from "../config/roles";
import { hhmm, STATUS_COLOR, todayStr } from "../config/constants";
import {
  getAllBills,
  getAllDoctors,
  getAllMedicines,
  getAllPatients,
  getAppointmentsByDate,
  getAppointmentsByDoctor,
  getAppointmentsByPatient,
} from "../services/ApiService";

// a medicine with fewer units than this is shown as low in stock
const LOW_STOCK_LIMIT = 20;

const money = (n) => `₹ ${Number(n).toFixed(2)}`;
// ADMINISTRATOR / Administrator -> Administrator
const roleLabel = (r) => r.charAt(0).toUpperCase() + r.slice(1).toLowerCase();
const pendingOf = (bills) => bills.filter((b) => b.status === "PENDING");
const lowStockOf = (medicines) => medicines.filter((m) => m.availability < LOW_STOCK_LIMIT);

function billsCard(pending) {
  let total = 0;
  pending.forEach((b) => (total += Number(b.amount)));
  return { label: "Pending bills", value: pending.length, sub: money(total), icon: "bi-receipt", color: "danger", path: PATHS.bills };
}

function lowStockCard(low) {
  return {
    label: "Low stock medicines",
    value: low.length,
    sub: `below ${LOW_STOCK_LIMIT} units`,
    icon: "bi-capsule",
    color: "warning",
    path: PATHS.medicines,
  };
}

// returns the stat cards and the two lower panels for the logged-in role
function loadDashboard(user) {
  const role = user.role.toUpperCase(); // so it works whether the backend sends ADMINISTRATOR or Administrator
  const { profileId } = user;

  if (role === "ADMINISTRATOR") {
    return Promise.all([getAllPatients(), getAllDoctors(), getAppointmentsByDate(todayStr()), getAllBills(), getAllMedicines()]).then(
      ([p, d, a, b, m]) => {
        const low = lowStockOf(m);
        return {
          stats: [
            { label: "Patients", value: p.length, icon: "bi-people", color: "primary", path: PATHS.patients },
            { label: "Doctors", value: d.length, icon: "bi-person-badge", color: "success", path: PATHS.doctors },
            { label: "Today's appointments", value: a.length, icon: "bi-calendar-check", color: "info", path: PATHS.appointments },
            billsCard(pendingOf(b)),
            lowStockCard(low),
          ],
          appointments: { title: "Today's appointments", list: a },
          side: { type: "lowStock", list: low },
        };
      }
    );
  }

  if (role === "RECEPTIONIST") {
    return Promise.all([getAllPatients(), getAllDoctors(), getAppointmentsByDate(todayStr()), getAllBills()]).then(([p, d, a, b]) => {
      const pending = pendingOf(b);
      return {
        stats: [
          { label: "Patients", value: p.length, icon: "bi-people", color: "primary", path: PATHS.patients },
          { label: "Doctors", value: d.length, icon: "bi-person-badge", color: "success", path: PATHS.doctors },
          { label: "Today's appointments", value: a.length, icon: "bi-calendar-check", color: "info", path: PATHS.appointments },
          billsCard(pending),
        ],
        appointments: { title: "Today's appointments", list: a },
        side: { type: "bills", list: pending },
      };
    });
  }

  if (role === "PHARMACIST") {
    return getAllMedicines().then((m) => {
      const low = lowStockOf(m);
      return {
        stats: [
          { label: "Medicines", value: m.length, icon: "bi-capsule", color: "primary", path: PATHS.medicines },
          lowStockCard(low),
        ],
        appointments: null,
        side: { type: "lowStock", list: low },
      };
    });
  }

  if (role === "DOCTOR" && profileId) {
    return getAppointmentsByDoctor(profileId).then((list) => ({
      stats: [
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
      ],
      appointments: { title: "Today's appointments", list: list.filter((a) => a.date === todayStr()) },
      side: null,
    }));
  }

  if (role === "PATIENT" && profileId) {
    return getAppointmentsByPatient(profileId).then((list) => {
      // the list comes newest first, so reverse it to show the nearest upcoming one first
      const upcoming = list.filter((a) => a.status === "Scheduled").reverse();
      return {
        stats: [
          { label: "All appointments", value: list.length, icon: "bi-calendar3", color: "primary", path: PATHS.appointments },
          { label: "Upcoming", value: upcoming.length, icon: "bi-calendar-day", color: "warning", path: PATHS.appointments },
        ],
        appointments: { title: "Upcoming appointments", list: upcoming, showDate: true },
        side: null,
      };
    });
  }

  return Promise.resolve({ stats: [], appointments: null, side: null });
}

export default function Dashboard() {
  const { user } = useAuth();
  const { data: dash, loading, error } = useApi(() => loadDashboard(user), [user.role, user.profileId]);
  const missingProfile = ["PATIENT", "DOCTOR"].includes(user.role.toUpperCase()) && !user.profileId;
  const longDate = new Date().toLocaleDateString(undefined, {
    weekday: "long", day: "numeric", month: "long", year: "numeric",
  });

  const appts = dash && dash.appointments;
  const side = dash && dash.side;

  return (
    <div>
      <div className="welcome-panel d-flex justify-content-between align-items-center mb-4">
        <div>
          <h4 className="mb-1">Welcome back, {roleLabel(user.role)}</h4>
          <span className="opacity-75">{longDate}</span>
        </div>
        <i className="bi bi-heart-pulse welcome-icon d-none d-sm-block"></i>
      </div>

      {missingProfile && (
        <div className="alert alert-warning">
          No {roleLabel(user.role).toLowerCase()} profile is linked to this login, so personal data cannot be shown.
        </div>
      )}
      {error && <div className="alert alert-danger">{error}</div>}
      {loading && <Loader />}

      {!loading && dash && dash.stats.length > 0 && (
        <div className="row g-3 mb-4">
          {dash.stats.map((s) => {
            const inner = (
              <div className="card-body d-flex align-items-center gap-3">
                <span className={`stat-icon ${s.color}`}><i className={`bi ${s.icon}`}></i></span>
                <div>
                  <div className="fs-3 fw-bold lh-1">{s.value}</div>
                  <div className="text-muted small mt-1">{s.label}</div>
                  {s.sub && <div className="text-muted small">{s.sub}</div>}
                </div>
              </div>
            );
            return (
              <div className="col-12 col-sm-6 col-xl" key={s.label}>
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

      {!loading && (appts || side) && (
        <div className="row g-3">
          {appts && (
            <div className={side ? "col-12 col-xl-7" : "col-12"}>
              <div className="card border-0 shadow-sm h-100">
                <div className="card-header bg-white d-flex justify-content-between align-items-center">
                  <span><i className="bi bi-calendar-check me-2 text-primary"></i>{appts.title}</span>
                  {canAccess(user.role, PATHS.appointments) && (
                    <Link to={PATHS.appointments} className="small text-decoration-none">View all</Link>
                  )}
                </div>
                <div className="table-responsive">
                  <table className="table table-hover align-middle mb-0">
                    <thead className="table-light">
                      <tr>
                        {appts.showDate && <th>Date</th>}
                        <th>Time</th><th>Patient</th><th>Doctor</th><th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {appts.list.slice(0, 6).map((a) => (
                        <tr key={a.appointmentId}>
                          {appts.showDate && <td>{a.date}</td>}
                          <td>{hhmm(a.startTime)} - {hhmm(a.endTime)}</td>
                          <td>{a.patientName ?? a.patientId}</td>
                          <td>{a.doctorName ?? a.doctorId}</td>
                          <td><span className={`badge text-bg-${STATUS_COLOR[a.status] || "secondary"}`}>{a.status}</span></td>
                        </tr>
                      ))}
                      {appts.list.length === 0 && (
                        <tr><td colSpan="5" className="text-center text-muted py-4">No appointments</td></tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {side && (
            <div className={appts ? "col-12 col-xl-5" : "col-12"}>
              <div className="card border-0 shadow-sm h-100">
                <div className="card-header bg-white d-flex justify-content-between align-items-center">
                  <span>
                    <i className={`bi ${side.type === "bills" ? "bi-receipt" : "bi-capsule"} me-2 text-primary`}></i>
                    {side.type === "bills" ? "Pending bills" : "Medicines low in stock"}
                  </span>
                  {canAccess(user.role, side.type === "bills" ? PATHS.bills : PATHS.medicines) && (
                    <Link to={side.type === "bills" ? PATHS.bills : PATHS.medicines} className="small text-decoration-none">
                      View all
                    </Link>
                  )}
                </div>
                <ul className="list-group list-group-flush">
                  {side.list.slice(0, 6).map((item) =>
                    side.type === "bills" ? (
                      <li className="list-group-item d-flex justify-content-between" key={item.billId}>
                        <span>#{item.billId} {item.patientName}</span>
                        <span className="fw-semibold">{money(item.amount)}</span>
                      </li>
                    ) : (
                      <li className="list-group-item d-flex justify-content-between" key={item.medicineId}>
                        <span>{item.medicineName}</span>
                        <span className={`badge text-bg-${item.availability === 0 ? "danger" : "warning"}`}>
                          {item.availability} left
                        </span>
                      </li>
                    )
                  )}
                  {side.list.length === 0 && (
                    <li className="list-group-item text-center text-muted py-4">
                      {side.type === "bills" ? "No pending bills" : "All medicines are well stocked"}
                    </li>
                  )}
                </ul>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}