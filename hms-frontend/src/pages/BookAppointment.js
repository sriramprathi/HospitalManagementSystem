import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Field from "../components/Field";
import { PATHS } from "../config/roles";
import { DEPARTMENTS, departmentLabel, todayStr } from "../config/constants";
import { bookAppointment, getAllDoctors, getDoctorsByDepartment } from "../services/ApiService";

export default function BookAppointment() {
  const { user } = useAuth();
  const isPatient = user.role === "PATIENT";

  const [dept, setDept] = useState("");
  const [patientId, setPatientId] = useState(isPatient ? user.profileId || "" : "");
  const [doctorId, setDoctorId] = useState("");
  const [date, setDate] = useState(todayStr());
  const [startTime, setStartTime] = useState("");
  const [disease, setDisease] = useState("");
  const [error, setError] = useState("");
  const [booked, setBooked] = useState(null);
  const [saving, setSaving] = useState(false);

  const { data: doctors } = useApi(() => (dept ? getDoctorsByDepartment(dept) : getAllDoctors()), [dept]);

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    setBooked(null);
    setSaving(true);
    try {
      const res = await bookAppointment({
        patientId: Number(patientId),
        doctorId: Number(doctorId),
        date,
        startTime,
        disease,
      });
      setBooked(res);
      setStartTime("");
      setDisease("");
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={{ maxWidth: 720 }}>
      <h4 className="mb-3">Book appointment</h4>

      {isPatient && !user.profileId && (
        <div className="alert alert-warning">No patient profile is linked to this login.</div>
      )}

      {booked && (
        <div className="alert alert-success">
          Appointment #{booked.appointmentId} booked for {booked.date}. <Link to={PATHS.appointments}>View appointments</Link>
        </div>
      )}

      <form className="card border-0 shadow-sm" onSubmit={submit}>
        <div className="card-body">
          <div className="row g-3">
            {!isPatient && (
              <Field label="Patient ID" col="col-12" required>
                <input type="number" min="1" className="form-control" value={patientId}
                  onChange={(e) => setPatientId(e.target.value)} required />
              </Field>
            )}
            <Field label="Department">
              <select className="form-select" value={dept} onChange={(e) => { setDept(e.target.value); setDoctorId(""); }}>
                <option value="">All departments</option>
                {DEPARTMENTS.map((d) => <option key={d} value={d}>{departmentLabel(d)}</option>)}
              </select>
            </Field>
            <Field label="Doctor" required>
              <select className="form-select" value={doctorId} onChange={(e) => setDoctorId(e.target.value)} required>
                <option value="">Select a doctor</option>
                {(doctors || []).map((d) => (
                  <option key={d.doctorId} value={d.doctorId}>
                    Dr. {d.firstName} {d.lastName} ({d.department})
                  </option>
                ))}
              </select>
            </Field>
            <Field label="Date" required>
              <input type="date" min={todayStr()} className="form-control" value={date}
                onChange={(e) => setDate(e.target.value)} required />
            </Field>
            <Field label="Start time (30 min slot)" required>
              <input type="time" step="1800" className="form-control" value={startTime}
                onChange={(e) => setStartTime(e.target.value)} required />
            </Field>
            <Field label="Reason / disease" col="col-12">
              <input className="form-control" value={disease} maxLength={100}
                onChange={(e) => setDisease(e.target.value)} />
            </Field>
          </div>

          {error && <div className="alert alert-danger mt-3 mb-0">{error}</div>}

          <div className="mt-3">
            <button type="submit" className="btn btn-primary" disabled={saving || (isPatient && !user.profileId)}>
              {saving ? "Booking..." : "Book appointment"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
