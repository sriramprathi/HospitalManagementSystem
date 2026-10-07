import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Field from "../components/Field";
import { PATHS } from "../config/roles";
import { DEPARTMENTS, departmentLabel, todayStr } from "../config/constants";
import { bookAppointment, getAllDoctors, getAvailableSlots, getDoctorsByDepartment } from "../services/ApiService";

// 14:00:00 -> 2:00 PM
const slotLabel = (t) => {
  const [h, m] = String(t).split(":").map(Number);
  return `${h % 12 || 12}:${String(m).padStart(2, "0")} ${h < 12 ? "AM" : "PM"}`;
};

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
  const [slots, setSlots] = useState([]);
  const [slotsLoading, setSlotsLoading] = useState(false);

  const { data: doctors } = useApi(() => (dept ? getDoctorsByDepartment(dept) : getAllDoctors()), [dept]);

  const loadSlots = () => {
    if (!doctorId || !date) return Promise.resolve();
    setSlotsLoading(true);
    return getAvailableSlots(doctorId, date)
      .then(setSlots)
      .catch((err) => setError(err.message))
      .finally(() => setSlotsLoading(false));
  };

  // whenever the doctor or the date changes, clear the chosen time and load the free slots
  useEffect(() => {
    setError("");
    setStartTime("");
    setSlots([]);
    loadSlots();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [doctorId, date]);

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
      loadSlots(); // the slot just booked disappears from the list
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
            <Field label="Time slot" col="col-12" required>
              {!doctorId ? (
                <div className="text-muted">Select a doctor and a date to see the free slots.</div>
              ) : slotsLoading ? (
                <div className="text-muted">Loading slots...</div>
              ) : slots.length === 0 ? (
                <div className="text-muted">No free slots on this day. Please choose another date.</div>
              ) : (
                <div className="d-flex flex-wrap gap-2">
                  {slots.map((t) => (
                    <button
                      type="button"
                      key={t}
                      className={`btn btn-sm ${startTime === t ? "btn-primary" : "btn-outline-primary"}`}
                      onClick={() => setStartTime(t)}
                    >
                      {slotLabel(t)}
                    </button>
                  ))}
                </div>
              )}
              <small className="text-muted d-block mt-2">
                Open 10:00 AM to 4:00 PM, 30-minute slots, lunch break 1:00 PM to 2:00 PM.
              </small>
            </Field>
            <Field label="Reason / disease" col="col-12">
              <input className="form-control" value={disease} maxLength={100}
                onChange={(e) => setDisease(e.target.value)} />
            </Field>
          </div>

          {error && <div className="alert alert-danger mt-3 mb-0">{error}</div>}

          <div className="mt-3">
            <button type="submit" className="btn btn-primary" disabled={saving || !startTime || (isPatient && !user.profileId)}>
              {saving ? "Booking..." : "Book appointment"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
