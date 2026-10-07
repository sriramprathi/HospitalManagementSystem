import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import Field from "../components/Field";
import { deleteUser, getUsersByRole, registerUser } from "../services/ApiService";

// roles an administrator can create accounts for.
// These must match the backend Role enum exactly (same spelling and same capital letters)
const STAFF_ROLES = ["ADMINISTRATOR", "RECEPTIONIST", "PHARMACIST"];
const ROLE_COLOR = { ADMINISTRATOR: "dark", RECEPTIONIST: "info", PHARMACIST: "success" };
const EMPTY = { firstName: "", lastName: "", role: "RECEPTIONIST" };
// RECEPTIONIST -> Receptionist
const label = (r) => r.charAt(0).toUpperCase() + r.slice(1).toLowerCase();

export default function Staff() {
  const { user } = useAuth();

  const [roleFilter, setRoleFilter] = useState("");
  const [form, setForm] = useState(null); // form values while the add modal is open
  const [formError, setFormError] = useState("");
  const [actionError, setActionError] = useState("");
  const [saving, setSaving] = useState(false);
  const [creds, setCreds] = useState(null); // login details shown once after creating an account

  const { data, loading, error, reload } = useApi(
    () =>
      Promise.all(STAFF_ROLES.map((r) => getUsersByRole(r))).then((lists) => {
        const all = [];
        lists.forEach((list, i) => list.forEach((u) => all.push({ ...u, role: STAFF_ROLES[i] })));
        return all;
      }),
    []
  );

  const all = data || [];
  const list = all.filter((u) => !roleFilter || u.role === roleFilter);

  const set = (key) => (e) => setForm({ ...form, [key]: e.target.value });

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    try {
      setCreds(await registerUser(form));
      setForm(null);
      reload();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (u) => {
    if (!window.confirm(`Delete the account ${u.userName ?? u.username}?`)) return;
    setActionError("");
    try {
      await deleteUser(u.userId);
      reload();
    } catch (err) {
      setActionError(err.message);
    }
  };

  return (
    <div>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h4 className="mb-0"><i className="bi bi-person-lines-fill me-2 text-primary"></i>Staff</h4>
        <div className="d-flex gap-2">
          <select className="form-select" value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
            <option value="">All roles</option>
            {STAFF_ROLES.map((r) => <option key={r} value={r}>{label(r)}</option>)}
          </select>
          <button
            className="btn btn-primary text-nowrap"
            onClick={() => {
              setFormError("");
              setForm(EMPTY);
            }}
          >
            <i className="bi bi-plus-lg me-1"></i>Add staff
          </button>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {actionError && <div className="alert alert-danger">{actionError}</div>}

      {loading ? (
        <Loader />
      ) : (
        <>
          <div className="row g-3 mb-4">
            {STAFF_ROLES.map((r) => (
              <div className="col-12 col-md-4" key={r}>
                <div className="card border-0 shadow-sm h-100">
                  <div className="card-body d-flex justify-content-between align-items-center">
                    <span className="text-muted">{label(r)}s</span>
                    <span className="fs-3 fw-bold">{all.filter((u) => u.role === r).length}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>

          <div className="card border-0 shadow-sm">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr><th>ID</th><th>Username</th><th>Role</th><th className="text-end">Actions</th></tr>
                </thead>
                <tbody>
                  {list.map((u) => (
                    <tr key={u.userId}>
                      <td>{u.userId}</td>
                      <td>{u.userName ?? u.username}</td>
                      <td><span className={`badge text-bg-${ROLE_COLOR[u.role] || "secondary"}`}>{label(u.role)}</span></td>
                      <td className="text-end">
                        {u.userId !== user.userId && (
                          <button className="btn btn-sm btn-outline-danger" title="Delete account" onClick={() => remove(u)}>
                            <i className="bi bi-trash"></i>
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                  {list.length === 0 && (
                    <tr><td colSpan="4" className="text-center text-muted py-4">No staff accounts found</td></tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}

      {form && (
        <Modal title="Add staff account" onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="row g-3">
              <Field label="First name" required>
                <input className="form-control" value={form.firstName} onChange={set("firstName")}
                  pattern="[A-Za-z]+" title="Letters only, no spaces" required />
              </Field>
              <Field label="Last name" required>
                <input className="form-control" value={form.lastName} onChange={set("lastName")}
                  pattern="[A-Za-z]+" title="Letters only, no spaces" required />
              </Field>
              <Field label="Role" col="col-12" required>
                <select className="form-select" value={form.role} onChange={set("role")}>
                  {STAFF_ROLES.map((r) => <option key={r} value={r}>{label(r)}</option>)}
                </select>
              </Field>
            </div>
            {formError && <div className="alert alert-danger mt-3 mb-0">{formError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setForm(null)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Creating..." : "Create account"}</button>
            </div>
          </form>
        </Modal>
      )}

      {creds && (
        <Modal title="Account created" onClose={() => setCreds(null)}>
          <p>Give these login details to the staff member. The password is shown only now.</p>
          <ul className="list-group mb-3">
            <li className="list-group-item"><b>Username:</b> {creds.userName}</li>
            <li className="list-group-item"><b>Password:</b> {creds.password}</li>
          </ul>
          <button className="btn btn-primary" onClick={() => setCreds(null)}>Done</button>
        </Modal>
      )}
    </div>
  );
}