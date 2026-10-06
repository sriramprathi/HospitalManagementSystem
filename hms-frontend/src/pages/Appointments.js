import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import Field from "../components/Field";
import { canAccess, PATHS } from "../config/roles";
import { hhmm, STATUS_COLOR, STATUS, statusLabel, todayStr } from "../config/constants";
import {
  cancelAppointment,
  getAppointmentsByDate,
  getAppointmentsByDoctor,
  getAppointmentsByPatient,
  rescheduleAppointment,
  updateAppointmentStatus,
} from "../services/ApiService";

export default function Appointments() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const isDoctor = user.role === "DOCTOR";
  const isPatient = user.role === "PATIENT";
  const canComplete = isDoctor || user.role === "ADMINISTRATOR";
  const canSeePrescriptions = canAccess(user.role, PATHS.prescriptions);

  const [date, setDate] = useState(todayStr()); // used by Administrator / Receptionist
  const [statusFilter, setStatusFilter] = useState("");
  const [actionError, setActionError] = useState("");
  const [resched, setResched] = useState(null); // { id, date, startTime }
  const [reschedError, setReschedError] = useState("");

  const { data, loading, error, reload } = useApi(() => {
    if ((isDoctor || isPatient) && !user.profileId) return Promise.resolve([]);
    if (isDoctor) return getAppointmentsByDoctor(user.profileId);
    if (isPatient) return getAppointmentsByPatient(user.profileId);
    return getAppointmentsByDate(date);
  }, [date, user.role, user.profileId]);

  const list = (data || []).filter((a) => !statusFilter || a.status === statusFilter);

  const run = async (fn) => {
    setActionError("");
    try {
      await fn();
      reload();
    } catch (err) {
      setActionError(err.message);
    }
  };

  const cancel = (a) => {
    if (window.confirm("Cancel this appointment?")) run(() => cancelAppointment(a.appointmentId));
  };

  const submitResched = async (e) => {
    e.preventDefault();
    setReschedError("");
    try {
      await rescheduleAppointment(resched.id, { date: resched.date, startTime: resched.startTime });
      setResched(null);
      reload();
    } catch (err) {
      setReschedError(err.message);
    }
  };

  return (
    <div>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h4 className="mb-0">{isPatient ? "My appointments" : "Appointments"}</h4>
        <div className="d-flex gap-2">
          {!isDoctor && !isPatient && (
            <input type="date" className="form-control" value={date} onChange={(e) => setDate(e.target.value)} />
          )}
          <select className="form-select" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All statuses</option>
            {STATUS.map((status) => <option key={status} value={status}>{statusLabel(status)}</option>)}
          </select>
        </div>
      </div>

      {(isDoctor || isPatient) && !user.profileId && (
        <div className="alert alert-warning">No {user.role === "PATIENT" ? "patient" : "doctor"} profile is linked to this login.</div>
      )}
      {error && <div className="alert alert-danger">{error}</div>}
      {actionError && <div className="alert alert-danger">{actionError}</div>}

      {loading ? (
        <Loader />
      ) : (
        <div className="card border-0 shadow-sm">
          <div className="table-responsive">
            <table className="table table-hover align-middle mb-0">
              <thead className="table-light">
                <tr>
                  <th>#</th><th>Date</th><th>Time</th>
                  {!isPatient && <th>Patient</th>}
                  {!isDoctor && <th>Doctor</th>}
                  <th>Reason</th><th>Status</th><th className="text-end">Actions</th>
                </tr>
              </thead>
              <tbody>
                {list.map((a) => (
                  <tr key={a.appointmentId}>
                    <td>{a.appointmentId}</td>
                    <td>{a.date}</td>
                    <td>{hhmm(a.startTime)} - {hhmm(a.endTime)}</td>
                    {!isPatient && <td>{a.patientName ?? a.patientId}</td>}
                    {!isDoctor && <td>{a.doctorName ?? a.doctorId}</td>}
                    <td>{a.disease}</td>
                    <td><span className={`badge text-bg-${STATUS_COLOR[a.status] || "secondary"}`}>{statusLabel(a.status)}</span></td>
                    <td className="text-end text-nowrap">
                      {a.status === "SCHEDULED" && (
                        <>
                          {canComplete && (
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-success me-1"
                              title="Mark completed"
                              onClick={() => run(() => updateAppointmentStatus(a.appointmentId, "COMPLETED"))}
                            >
                              <i className="bi bi-check2"></i>
                            </button>
                          )}
                          <button
                            type="button"
                            className="btn btn-sm btn-outline-primary me-1"
                            title="Reschedule"
                            onClick={() => {
                              setReschedError("");
                              setResched({ id: a.appointmentId, date: a.date, startTime: hhmm(a.startTime) });
                            }}
                          >
                            <i className="bi bi-calendar2-week"></i>
                          </button>
                          <button type="button" className="btn btn-sm btn-outline-danger me-1" title="Cancel" onClick={() => cancel(a)}>
                            <i className="bi bi-x-lg"></i>
                          </button>
                        </>
                      )}
                      {canSeePrescriptions && a.status !== "CANCELLED" && (
                        <button
                          type="button"
                          className="btn btn-sm btn-outline-secondary"
                          title="Prescription"
                          onClick={() => navigate(`${PATHS.prescriptions}?appointmentId=${a.appointmentId}`)}
                        >
                          <i className="bi bi-file-medical"></i>
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
                {list.length === 0 && (
                  <tr><td colSpan="8" className="text-center text-muted py-4">No appointments found</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {resched && (
        <Modal title={`Reschedule appointment #${resched.id}`} onClose={() => setResched(null)}>
          <form onSubmit={submitResched}>
            <div className="row g-3">
              <Field label="New date">
                <input type="date" min={todayStr()} className="form-control" value={resched.date}
                  onChange={(e) => setResched({ ...resched, date: e.target.value })} required />
              </Field>
              <Field label="New start time">
                <input type="time" step="1800" className="form-control" value={resched.startTime}
                  onChange={(e) => setResched({ ...resched, startTime: e.target.value })} required />
              </Field>
            </div>
            <small className="text-muted d-block mt-2">Every appointment lasts 30 minutes.</small>
            {reschedError && <div className="alert alert-danger mt-3 mb-0">{reschedError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setResched(null)}>Close</button>
              <button type="submit" className="btn btn-primary">Reschedule</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
