import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import Field from "../components/Field";
import { addPatient, deletePatient, getAllPatients, updatePatient } from "../services/ApiService";
import { GENDERS, todayStr } from "../config/constants";

const EMPTY = {
  firstName: "",
  lastName: "",
  gender: "Male",
  dateOfBirth: "",
  phoneNumber: "",
};

export default function Patients() {
  const { user } = useAuth();
  const canEdit = user.role === "Administrator" || user.role === "Receptionist";
  const canDelete = user.role === "Administrator";

  const { data, loading, error, reload } = useApi(getAllPatients);
  const [search, setSearch] = useState("");
  const [form, setForm] = useState(null); // { id, values } while the modal is open
  const [formError, setFormError] = useState("");
  const [actionError, setActionError] = useState("");
  const [saving, setSaving] = useState(false);
  const [creds, setCreds] = useState(null); // login details shown once after registering

  const q = search.trim().toLowerCase();
  const patients = (data || []).filter(
    (p) => !q || `${p.patientId} ${p.firstName} ${p.lastName} ${p.phoneNumber}`.toLowerCase().includes(q)
  );

  const openAdd = () => {
    setFormError("");
    setForm({ id: null, values: EMPTY });
  };

  const openEdit = (p) => {
    setFormError("");
    const values = {};
    Object.keys(EMPTY).forEach((k) => (values[k] = p[k] ?? ""));
    setForm({ id: p.patientId, values });
  };

  const set = (key) => (e) => setForm({ ...form, values: { ...form.values, [key]: e.target.value } });

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    try {
      if (form.id) {
        await updatePatient(form.id, form.values);
      } else {
        setCreds(await addPatient(form.values));
      }
      setForm(null);
      reload();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (p) => {
    if (!window.confirm(`Delete ${p.firstName} ${p.lastName}? Their login is removed too.`)) return;
    setActionError("");
    try {
      await deletePatient(p.patientId);
      reload();
    } catch (err) {
      setActionError(err.message);
    }
  };

  return (
    <div>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h4 className="mb-0">Patients</h4>
        <div className="d-flex gap-2">
          <input
            className="form-control"
            placeholder="Search name, phone, id"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
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
                  <th>ID</th><th>Name</th><th>Gender</th><th>DOB</th><th>Phone</th>
                  {canEdit && <th className="text-end">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {patients.map((p) => (
                  <tr key={p.patientId}>
                    <td>{p.patientId}</td>
                    <td>{p.firstName} {p.lastName}</td>
                    <td>{p.gender}</td>
                    <td>{p.dateOfBirth}</td>
                    <td>{p.phoneNumber}</td>
                    {canEdit && (
                      <td className="text-end text-nowrap">
                        <button className="btn btn-sm btn-outline-primary me-1" onClick={() => openEdit(p)}>
                          <i className="bi bi-pencil"></i>
                        </button>
                        {canDelete && (
                          <button className="btn btn-sm btn-outline-danger" onClick={() => remove(p)}>
                            <i className="bi bi-trash"></i>
                          </button>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
                {patients.length === 0 && (
                  <tr><td colSpan="6" className="text-center text-muted py-4">No patients found</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {form && (
        <Modal title={form.id ? "Edit patient" : "Register patient"} size="modal-lg" onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="row g-3">
              <Field label="First name"><input className="form-control" value={form.values.firstName} onChange={set("firstName")} required /></Field>
              <Field label="Last name"><input className="form-control" value={form.values.lastName} onChange={set("lastName")} required /></Field>
              <Field label="Gender">
                <select className="form-select" value={form.values.gender} onChange={set("gender")}>
                  {GENDERS.map((g) => <option key={g}>{g}</option>)}
                </select>
              </Field>
              <Field label="Date of birth">
                <input type="date" max={todayStr()} className="form-control" value={form.values.dateOfBirth} onChange={set("dateOfBirth")} required />
              </Field>
              <Field label="Phone"><input className="form-control" value={form.values.phoneNumber} onChange={set("phoneNumber")} required /></Field>
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
        <Modal title="Patient registered" onClose={() => setCreds(null)}>
          <p>Share these login details with the patient. The password is shown only now.</p>
          <ul className="list-group mb-3">
            <li className="list-group-item"><b>Patient ID:</b> {creds.patientId}</li>
            <li className="list-group-item"><b>Username:</b> {creds.userName}</li>
            <li className="list-group-item"><b>Password:</b> {creds.password}</li>
          </ul>
          <button className="btn btn-primary" onClick={() => setCreds(null)}>Done</button>
        </Modal>
      )}
    </div>
  );
}