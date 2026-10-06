import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import Field from "../components/Field";
import { addMedicine, deleteMedicine, getAllMedicines, updateMedicine } from "../services/ApiService";
import { MEDICINE_CATEGORIES } from "../config/constants";

const EMPTY = { medicineName: "", price: "", availability: "", category: "TABLET" };

// TABLET -> Tablet
const categoryLabel = (c) => (c ? c.charAt(0) + c.slice(1).toLowerCase() : "");

export default function Medicines() {
  const { user } = useAuth();
  // who can do what (change these three lines to adjust)
  const canAdd = user.role === "PHARMACIST";
  const canEdit = user.role === "ADMINISTRATOR" || user.role === "PHARMACIST";
  const canDelete = user.role === "ADMINISTRATOR";

  const { data, loading, error, reload } = useApi(getAllMedicines);
  const [search, setSearch] = useState("");
  const [form, setForm] = useState(null); // { id, values } while the modal is open
  const [formError, setFormError] = useState("");
  const [actionError, setActionError] = useState("");
  const [saving, setSaving] = useState(false);

  const q = search.trim().toLowerCase();
  const medicines = (data || []).filter(
    (m) => !q || `${m.medicineId} ${m.medicineName} ${m.category}`.toLowerCase().includes(q)
  );

  const openAdd = () => {
    setFormError("");
    setForm({ id: null, values: EMPTY });
  };

  const openEdit = (m) => {
    setFormError("");
    setForm({
      id: m.medicineId,
      values: {
        medicineName: m.medicineName,
        price: m.price,
        availability: m.availability,
        category: m.category,
      },
    });
  };

  const set = (key) => (e) => setForm({ ...form, values: { ...form.values, [key]: e.target.value } });

  const save = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    const body = {
      ...form.values,
      medicineName: form.values.medicineName.trim(),
      price: Number(form.values.price),
      availability: parseInt(form.values.availability, 10),
    };
    try {
      if (form.id) {
        await updateMedicine(form.id, body);
      } else {
        await addMedicine(body);
      }
      setForm(null);
      reload();
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (m) => {
    if (!window.confirm(`Delete ${m.medicineName}?`)) return;
    setActionError("");
    try {
      await deleteMedicine(m.medicineId);
      reload();
    } catch (err) {
      setActionError(err.message);
    }
  };

  return (
    <div>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h4 className="mb-0">Medicines</h4>
        <div className="d-flex gap-2">
          <input
            className="form-control"
            placeholder="Search name, category, id"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          {canAdd && (
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
                  <th>ID</th><th>Name</th><th>Category</th><th>Price</th><th>Available</th>
                  {canEdit && <th className="text-end">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {medicines.map((m) => (
                  <tr key={m.medicineId}>
                    <td>{m.medicineId}</td>
                    <td>{m.medicineName}</td>
                    <td><span className="badge text-bg-info">{categoryLabel(m.category)}</span></td>
                    <td>₹{Number(m.price).toFixed(2)}</td>
                    <td>
                      {m.availability === 0 ? (
                        <span className="badge text-bg-danger">Out of stock</span>
                      ) : (
                        m.availability
                      )}
                    </td>
                    {canEdit && (
                      <td className="text-end text-nowrap">
                        <button className="btn btn-sm btn-outline-primary me-1" onClick={() => openEdit(m)}>
                          <i className="bi bi-pencil"></i>
                        </button>
                        {canDelete && (
                          <button className="btn btn-sm btn-outline-danger" onClick={() => remove(m)}>
                            <i className="bi bi-trash"></i>
                          </button>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
                {medicines.length === 0 && (
                  <tr><td colSpan="6" className="text-center text-muted py-4">No medicines found</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {form && (
        <Modal title={form.id ? "Edit medicine" : "Add medicine"} onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="row g-3">
              <Field label="Medicine name" col="col-12" required>
                <input
                  className="form-control"
                  maxLength={40}
                  value={form.values.medicineName}
                  onChange={set("medicineName")}
                  required
                />
              </Field>
              <Field label="Category" required>
                <select className="form-select" value={form.values.category} onChange={set("category")}>
                  {MEDICINE_CATEGORIES.map((c) => <option key={c} value={c}>{categoryLabel(c)}</option>)}
                </select>
              </Field>
              <Field label="Price (₹)" required>
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  className="form-control"
                  value={form.values.price}
                  onChange={set("price")}
                  required
                />
              </Field>
              <Field label="Available quantity" required>
                <input
                  type="number"
                  min="0"
                  step="1"
                  className="form-control"
                  value={form.values.availability}
                  onChange={set("availability")}
                  required
                />
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
    </div>
  );
}