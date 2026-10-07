import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import useApi from "../hooks/useApi";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import Field from "../components/Field";
import {
  generateConsultationBill,
  generateMedicineBill,
  getAllBills,
  getBillsByPatient,
  updateBillStatus,
} from "../services/ApiService";

// bill status values come from the backend BillStatus enum
const BILL_COLOR = { PENDING: "warning", PAID: "success", CANCELLED: "secondary" };
const star = <span className="text-danger">*</span>;

export default function Bills() {
  const { user } = useAuth();
  const role = user.role.toUpperCase();
  const isPatient = role === "PATIENT";
  const canManage =  role === "RECEPTIONIST";

  const [statusFilter, setStatusFilter] = useState("");
  const [actionError, setActionError] = useState("");
  const [gen, setGen] = useState(null); // { type, id } while the generate-bill modal is open
  const [genError, setGenError] = useState("");
  const [saving, setSaving] = useState(false);

  const { data, loading, error, reload } = useApi(() => {
    if (isPatient) return user.profileId ? getBillsByPatient(user.profileId) : Promise.resolve([]);
    return getAllBills();
  }, [user.role, user.profileId]);

  const list = (data || []).filter((b) => !statusFilter || b.status === statusFilter);

  const changeStatus = async (bill, status, question) => {
    if (!window.confirm(question)) return;
    setActionError("");
    try {
      await updateBillStatus(bill.billId, status);
      reload();
    } catch (err) {
      setActionError(err.message);
    }
  };

  const generate = async (e) => {
    e.preventDefault();
    setSaving(true);
    setGenError("");
    try {
      if (gen.type === "CONSULTATION") await generateConsultationBill(Number(gen.id));
      else await generateMedicineBill(Number(gen.id));
      setGen(null);
      reload();
    } catch (err) {
      setGenError(err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h4 className="mb-0">
          <i className="bi bi-receipt me-2 text-primary"></i>
          {isPatient ? "My bills" : "Bills"}
        </h4>
        <div className="d-flex gap-2">
          <select className="form-select" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option value="">All statuses</option>
            <option value="PENDING">Pending</option>
            <option value="PAID">Paid</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          {canManage && (
            <button
              className="btn btn-primary text-nowrap"
              onClick={() => {
                setGenError("");
                setGen({ type: "CONSULTATION", id: "" });
              }}
            >
              <i className="bi bi-plus-lg me-1"></i>Generate
            </button>
          )}
        </div>
      </div>

      {isPatient && !user.profileId && (
        <div className="alert alert-warning">No patient profile is linked to this login.</div>
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
                  <th>#</th><th>Date</th>
                  {!isPatient && <th>Patient</th>}
                  <th>Type</th><th className="text-end">Amount</th><th>Status</th>
                  {canManage && <th className="text-end">Actions</th>}
                </tr>
              </thead>
              <tbody>
                {list.map((b) => (
                  <tr key={b.billId}>
                    <td>{b.billId}</td>
                    <td>{b.dateOfGeneration}</td>
                    {!isPatient && <td>{b.patientName ?? b.patientId}</td>}
                    <td>
                      <span className="badge text-bg-light border">
                        <i className={`bi ${b.billType === "MEDICINE" ? "bi-capsule" : "bi-person-badge"} me-1`}></i>
                        {b.billType === "MEDICINE" ? "Medicine" : "Consultation"}
                      </span>
                    </td>
                    <td className="text-end fw-semibold">₹ {Number(b.amount).toFixed(2)}</td>
                    <td><span className={`badge text-bg-${BILL_COLOR[b.status] || "secondary"}`}>{b.status}</span></td>
                    {canManage && (
                      <td className="text-end text-nowrap">
                        {b.status === "PENDING" && (
                          <>
                            <button
                              className="btn btn-sm btn-outline-success me-1"
                              title="Mark as paid"
                              onClick={() => changeStatus(b, "PAID", `Mark bill #${b.billId} as paid?`)}
                            >
                              <i className="bi bi-check2"></i>
                            </button>
                            <button
                              className="btn btn-sm btn-outline-danger"
                              title="Cancel bill"
                              onClick={() => changeStatus(b, "CANCELLED", `Cancel bill #${b.billId}?`)}
                            >
                              <i className="bi bi-x-lg"></i>
                            </button>
                          </>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
                {list.length === 0 && (
                  <tr><td colSpan="7" className="text-center text-muted py-4">No bills found</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {gen && (
        <Modal title="Generate bill" onClose={() => setGen(null)}>
          <form onSubmit={generate}>
            <div className="row g-3">
              <Field label={<>Bill type {star}</>}>
                <select className="form-select" value={gen.type} onChange={(e) => setGen({ ...gen, type: e.target.value })}>
                  <option value="CONSULTATION">Consultation</option>
                  <option value="MEDICINE">Medicine</option>
                </select>
              </Field>
              <Field label={<>{gen.type === "CONSULTATION" ? "Appointment ID" : "Prescription ID"} {star}</>}>
                <input type="number" min="1" className="form-control" value={gen.id}
                  onChange={(e) => setGen({ ...gen, id: e.target.value })} required />
              </Field>
            </div>
            <small className="text-muted d-block mt-2">
              Consultation bills use the doctor's fee. Medicine bills add up price x quantity of each medicine.
            </small>
            {genError && <div className="alert alert-danger mt-3 mb-0">{genError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setGen(null)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Generating..." : "Generate"}</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}