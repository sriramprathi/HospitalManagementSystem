import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import {
  createPrescription,
  deletePrescription,
  getAllMedicines,
  getPrescriptionByAppointment,
  getPrescriptionsByPatient,
  updatePrescription,
} from "../services/ApiService";

const BLANK_ITEM = {
  medicineId: "",
  quantity: 1,
  duration: 5,
  frequency: 2,
};

export default function Prescriptions() {
  const { user } = useAuth();
  const [params] = useSearchParams();

  const isPatient = user.role === "PATIENT";
  const canWrite =
    user.role === "DOCTOR" || user.role === "ADMINISTRATOR";

  const [mode, setMode] = useState("appointment");
  const [idInput, setIdInput] = useState(
    params.get("appointmentId") || ""
  );

  const [results, setResults] = useState([]);
  const [medicines, setMedicines] = useState([]);
  const [loading, setLoading] = useState(false);
  const [medicineLoading, setMedicineLoading] = useState(false);
  const [error, setError] = useState("");
  const [medicineError, setMedicineError] = useState("");
  const [searchedAppt, setSearchedAppt] = useState(null);

  const [form, setForm] = useState(null);
  const [formError, setFormError] = useState("");
  const [saving, setSaving] = useState(false);

  // Load all medicines for the prescription dropdown
  useEffect(() => {
    const loadMedicines = async () => {
      setMedicineLoading(true);
      setMedicineError("");

      try {
        const data = await getAllMedicines();
        setMedicines(data || []);
      } catch (err) {
        setMedicineError(err.message);
      } finally {
        setMedicineLoading(false);
      }
    };

    loadMedicines();
  }, []);

  const load = async (m, id) => {
    setLoading(true);
    setError("");
    setResults([]);
    setSearchedAppt(m === "appointment" ? id : null);

    try {
      if (m === "appointment") {
        setResults([await getPrescriptionByAppointment(id)]);
      } else {
        setResults(await getPrescriptionsByPatient(id));
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Patients see their own list.
  // Others can arrive from the appointments page with ?appointmentId=
  useEffect(() => {
    if (isPatient && user.profileId) {
      load("patient", user.profileId);
    } else if (!isPatient && params.get("appointmentId")) {
      load("appointment", params.get("appointmentId"));
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const onSearch = (e) => {
    e.preventDefault();
    load(mode, idInput);
  };

  const openCreate = () => {
    setFormError("");

    setForm({
      id: null,
      appointmentId: searchedAppt,
      notes: "",
      items: [{ ...BLANK_ITEM }],
    });
  };

  const openEdit = (p) => {
    setFormError("");

    setForm({
      id: p.prescriptionId,
      appointmentId: p.appointmentId,
      notes: p.notes || "",
      items: p.items.map((i) => ({
        medicineId: i.medicineId,
        quantity: i.quantity,
        duration: i.duration,
        frequency: i.frequency,
      })),
    });
  };

  const setItem = (index, key, value) => {
    setForm({
      ...form,
      items: form.items.map((it, i) =>
        i === index ? { ...it, [key]: value } : it
      ),
    });
  };

  const save = async (e) => {
    e.preventDefault();

    setSaving(true);
    setFormError("");

    const body = {
      notes: form.notes,
      items: form.items.map((i) => ({
        medicineId: Number(i.medicineId),
        quantity: Number(i.quantity),
        duration: Number(i.duration),
        frequency: Number(i.frequency),
      })),
    };

    try {
      if (form.id) {
        await updatePrescription(form.id, body);
      } else {
        await createPrescription(form.appointmentId, body);
      }

      const apptId = form.appointmentId;

      setForm(null);

      load("appointment", apptId);
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const remove = async (p) => {
    if (
      !window.confirm(
        `Delete prescription #${p.prescriptionId}?`
      )
    ) {
      return;
    }

    try {
      await deletePrescription(p.prescriptionId);

      setResults(
        results.filter(
          (r) => r.prescriptionId !== p.prescriptionId
        )
      );
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h4 className="mb-3">
        {isPatient ? "My prescriptions" : "Prescriptions"}
      </h4>

      {!isPatient && (
        <form
          className="card border-0 shadow-sm mb-3"
          onSubmit={onSearch}
        >
          <div className="card-body d-flex flex-wrap gap-2">
            <select
              className="form-select w-auto"
              value={mode}
              onChange={(e) => setMode(e.target.value)}
            >
              <option value="appointment">
                By appointment ID
              </option>

              <option value="patient">
                By patient ID
              </option>
            </select>

            <input
              type="number"
              min="1"
              className="form-control w-auto"
              placeholder="ID"
              value={idInput}
              onChange={(e) => setIdInput(e.target.value)}
              required
            />

            <button type="submit" className="btn btn-primary">
              <i className="bi bi-search me-1"></i>
              Search
            </button>
          </div>
        </form>
      )}

      {isPatient && !user.profileId && (
        <div className="alert alert-warning">
          No patient profile is linked to this login.
        </div>
      )}

      {medicineError && canWrite && (
        <div className="alert alert-danger">
          {medicineError}
        </div>
      )}

      {error && (
        <div className="alert alert-danger d-flex justify-content-between align-items-center">
          <span>{error}</span>

          {canWrite && searchedAppt && (
            <button
              type="button"
              className="btn btn-sm btn-primary"
              onClick={openCreate}
              disabled={
                medicineLoading || medicines.length === 0
              }
            >
              Create prescription
            </button>
          )}
        </div>
      )}

      {loading && <Loader />}

      {!loading &&
        results.length === 0 &&
        !error &&
        searchedAppt && (
          <div className="alert alert-info d-flex justify-content-between align-items-center">
            <span>
              No prescription found for appointment #
              {searchedAppt}.
            </span>

            {canWrite && (
              <button
                type="button"
                className="btn btn-primary"
                onClick={openCreate}
                disabled={
                  medicineLoading || medicines.length === 0
                }
              >
                Create prescription
              </button>
            )}
          </div>
        )}

      {results.map((p) => (
        <div
          className="card border-0 shadow-sm mb-3"
          key={p.prescriptionId}
        >
          <div className="card-header bg-white d-flex justify-content-between align-items-center">
            <b>Prescription #{p.prescriptionId}</b>

            {canWrite && (
              <div>
                <button
                  type="button"
                  className="btn btn-sm btn-outline-primary me-1"
                  onClick={() => openEdit(p)}
                >
                  <i className="bi bi-pencil"></i>
                </button>

                <button
                  type="button"
                  className="btn btn-sm btn-outline-danger"
                  onClick={() => remove(p)}
                >
                  <i className="bi bi-trash"></i>
                </button>
              </div>
            )}
          </div>

          <div className="card-body">
            <div className="row mb-2">
              <div className="col-md-4">
                <span className="text-muted">
                  Appointment:
                </span>{" "}
                #{p.appointmentId}
              </div>

              <div className="col-md-4">
                <span className="text-muted">
                  Patient:
                </span>{" "}
                {p.patientName}
              </div>

              <div className="col-md-4">
                <span className="text-muted">
                  Doctor:
                </span>{" "}
                {p.doctorName}
              </div>
            </div>

            {p.notes && (
              <p className="mb-3">
                <span className="text-muted">Notes:</span>{" "}
                {p.notes}
              </p>
            )}

            <div className="table-responsive">
              <table className="table table-sm mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Medicine</th>
                    <th>Quantity</th>
                    <th>Duration</th>
                    <th>Frequency</th>
                  </tr>
                </thead>

                <tbody>
                  {p.items.map((i) => (
                    <tr key={i.prescriptionItemId}>
                      <td>{i.medicineName}</td>
                      <td>{i.quantity}</td>
                      <td>{i.duration}</td>
                      <td>{i.frequency}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      ))}

      {form && (
        <Modal
          title={
            form.id
              ? `Edit prescription #${form.id}`
              : `New prescription for appointment #${form.appointmentId}`
          }
          size="modal-lg"
          onClose={() => setForm(null)}
        >
          <form onSubmit={save}>
            <label className="form-label">
              Notes
            </label>

            <textarea
              className="form-control mb-3"
              rows="2"
              maxLength={500}
              value={form.notes}
              onChange={(e) =>
                setForm({
                  ...form,
                  notes: e.target.value,
                })
              }
            />

            <label className="form-label">
              Medicines
            </label>

            {form.items.map((it, i) => (
              <div
                className="row g-2 mb-2"
                key={i}
              >
                {/* Medicine name dropdown */}
                <div className="col-4">
                  <select
                    className="form-select"
                    value={it.medicineId}
                    onChange={(e) =>
                      setItem(
                        i,
                        "medicineId",
                        e.target.value
                      )
                    }
                    required
                    disabled={medicineLoading}
                  >
                    <option value="">
                      {medicineLoading
                        ? "Loading medicines..."
                        : "Select medicine"}
                    </option>

                    {medicines.map((medicine) => (
                      <option
                        key={medicine.medicineId}
                        value={medicine.medicineId}
                      >
                        {medicine.medicineName}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Quantity */}
                <div className="col-2">
                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    placeholder="Qty *"
                    value={it.quantity}
                    onChange={(e) =>
                      setItem(
                        i,
                        "quantity",
                        e.target.value
                      )
                    }
                    required
                  />
                </div>

                {/* Duration */}
                <div className="col-2">
                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    placeholder="Duration *"
                    value={it.duration}
                    onChange={(e) =>
                      setItem(
                        i,
                        "duration",
                        e.target.value
                      )
                    }
                    required
                  />
                </div>

                {/* Frequency */}
                <div className="col-2">
                  <input
                    type="number"
                    min="1"
                    className="form-control"
                    placeholder="Frequency *"
                    value={it.frequency}
                    onChange={(e) =>
                      setItem(
                        i,
                        "frequency",
                        e.target.value
                      )
                    }
                    required
                  />
                </div>

                {/* Remove medicine */}
                <div className="col-2">
                  <button
                    type="button"
                    className="btn btn-outline-danger w-100"
                    disabled={form.items.length === 1}
                    onClick={() =>
                      setForm({
                        ...form,
                        items: form.items.filter(
                          (_, k) => k !== i
                        ),
                      })
                    }
                  >
                    <i className="bi bi-x"></i>
                  </button>
                </div>
              </div>
            ))}

            <small className="text-muted d-block mb-2">
              Select the medicine by name, then enter
              quantity, duration, and frequency.
            </small>

            <button
              type="button"
              className="btn btn-sm btn-outline-primary"
              onClick={() =>
                setForm({
                  ...form,
                  items: [
                    ...form.items,
                    { ...BLANK_ITEM },
                  ],
                })
              }
            >
              <i className="bi bi-plus"></i>{" "}
              Add medicine
            </button>

            {formError && (
              <div className="alert alert-danger mt-3 mb-0">
                {formError}
              </div>
            )}

            <div className="d-flex justify-content-end gap-2 mt-3">
              <button
                type="button"
                className="btn btn-light"
                onClick={() => setForm(null)}
              >
                Cancel
              </button>

              <button
                type="submit"
                className="btn btn-primary"
                disabled={
                  saving || medicineLoading
                }
              >
                {saving ? "Saving..." : "Save"}
              </button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}