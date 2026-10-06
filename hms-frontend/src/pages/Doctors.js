import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import Field from "../components/Field";
import { addDoctor, deleteDoctor, getAllDoctors, getDoctorsByDepartment, updateDoctor } from "../services/ApiService";
import { DEPARTMENTS, departmentLabel } from "../config/constants";

const EMPTY = { firstName: "", lastName: "", department: "CARDIOLOGY", qualification: "", phoneNumber: "", consultationFee: "" };

export default function Doctors() {
  const { user } = useAuth();
  const canEdit = user.role === "ADMINISTRATOR";

  const [dept, setDept] = useState("");
  const { data, loading, error, reload } = useApi(
    () => (dept ? getDoctorsByDepartment(dept) : getAllDoctors()),
    [dept]
  );
  const [form, setForm] = useState(null);
  const [formError, setFormError] = useState("");
  const [actionError, setActionError] = useState("");
  const [saving, setSaving] = useState(false);
  const [creds, setCreds] = useState(null);

  const openAdd = () => {
    setFormError("");
    setForm({ id: null, values: EMPTY });
  };

  const openEdit = (d) => {
    setFormError("");
    const values = {};
    Object.keys(EMPTY).forEach((k) => (values[k] = d[k] ?? ""));
    setForm({ id: d.doctorId, values });
  };

  const set = (key) => (e) => setForm({ ...form, values: { ...form.values, [key]: e.target.value } });

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    const body = { ...form.values, consultationFee: Number(form.values.consultationFee) };
    try {
      if (form.id) {
        await updateDoctor(form.id, body);
      } else {
        setCreds(await addDoctor(body));
      }
      setForm(null);
      reload();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (d) => {
    if (!window.confirm(`Delete Dr. ${d.firstName} ${d.lastName}? Their login is removed too.`)) return;
    setActionError("");
    try {
      await deleteDoctor(d.doctorId);
      reload();
    } catch (err) {
      setActionError(err.message);
    }
  };

  return (
    <div>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h4 className="mb-0">Doctors</h4>
        <div className="d-flex gap-2">
          <select className="form-select" value={dept} onChange={(e) => setDept(e.target.value)}>
            <option value="">All departments</option>
            {DEPARTMENTS.map((d) => <option key={d} value={d}>{departmentLabel(d)}</option>)}
          </select>
          {canEdit && (
            <button className="btn btn-primary text-nowrap" onClick={openAdd}>
              <i className="bi bi-plus-lg me-1"></i>Add
            </button>
          )}
        </div>
      </div>

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
                  <th>ID</th><th>Name</th><th>Department</th><th>Qualification</th><th>Phone</th><th>Fee</th>
                  {canEdit && <th className="text-end">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {(data || []).map((d) => (
                  <tr key={d.doctorId}>
                    <td>{d.doctorId}</td>
                    <td>Dr. {d.firstName} {d.lastName}</td>
                    <td><span className="badge text-bg-primary">{departmentLabel(d.department)}</span></td>
                    <td>{d.qualification}</td>
                    <td>{d.phoneNumber}</td>
                    <td>{d.consultationFee}</td>
                    {canEdit && (
                      <td className="text-end text-nowrap">
                        <button className="btn btn-sm btn-outline-primary me-1" onClick={() => openEdit(d)}>
                          <i className="bi bi-pencil"></i>
                        </button>
                        <button className="btn btn-sm btn-outline-danger" onClick={() => remove(d)}>
                          <i className="bi bi-trash"></i>
                        </button>
                      </td>
                    )}
                  </tr>
                ))}
                {(data || []).length === 0 && (
                  <tr><td colSpan="7" className="text-center text-muted py-4">No doctors found</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {form && (
        <Modal title={form.id ? "Edit doctor" : "Add doctor"} size="modal-lg" onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="row g-3">
              <Field label="First name" required><input className="form-control" value={form.values.firstName} onChange={set("firstName")} required /></Field>
              <Field label="Last name" required><input className="form-control" value={form.values.lastName} onChange={set("lastName")} required /></Field>
              <Field label="Department" required>
                <select className="form-select" value={form.values.department} onChange={set("department")}>
                  {DEPARTMENTS.map((d) => <option key={d} value={d}>{departmentLabel(d)}</option>)}
                </select>
              </Field>
              <Field label="Qualification" required><input className="form-control" value={form.values.qualification} onChange={set("qualification")} required /></Field>
              <Field label="Phone" required><input className="form-control" value={form.values.phoneNumber} onChange={set("phoneNumber")} required /></Field>
              <Field label="Consultation fee" required>
                <input type="number" min="0" step="0.01" className="form-control" value={form.values.consultationFee} onChange={set("consultationFee")} required />
              </Field>
            </div>
            {formError && <div className="alert alert-danger mt-3 mb-0">{formError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setForm(null)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Saving..." : "Save"}</button>
            </div>
          </form>
        </Modal>
      )}

      {creds && (
        <Modal title="Doctor added" onClose={() => setCreds(null)}>
          <p>Share these login details with the doctor. The password is shown only now.</p>
          <ul className="list-group mb-3">
            <li className="list-group-item"><b>Doctor ID:</b> {creds.doctorId}</li>
            <li className="list-group-item"><b>Username:</b> {creds.userName}</li>
            <li className="list-group-item"><b>Password:</b> {creds.password}</li>
          </ul>
          <button className="btn btn-primary" onClick={() => setCreds(null)}>Done</button>
        </Modal>
      )}
    </div>
  );
}
