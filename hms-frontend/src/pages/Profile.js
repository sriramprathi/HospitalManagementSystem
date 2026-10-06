import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import Field from "../components/Field";
import Loader from "../components/Loader";
import Modal from "../components/Modal";
import {
  changePassword,
  getAddressByUserId,
  getDoctorByUserId,
  getPatientByUserId,
  saveOrUpdateAddress,
  updateDoctor,
  updatePatient,
} from "../services/ApiService";
import { DEPARTMENTS, GENDERS, todayStr, departmentLabel } from "../config/constants";

const EMPTY_ADDRESS = { street: "", city: "", state: "" };

export default function Profile() {
  const { user } = useAuth();
  const isPatient = user.role === "PATIENT";
  const isDoctor = user.role === "DOCTOR";

  const [profile, setProfile] = useState(null);
  const [address, setAddress] = useState(EMPTY_ADDRESS);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [detailsOpen, setDetailsOpen] = useState(false);
  const [addressOpen, setAddressOpen] = useState(false);
  const [passwordOpen, setPasswordOpen] = useState(false);
  const [formError, setFormError] = useState("");
  const [saving, setSaving] = useState(false);
  const [passwords, setPasswords] = useState({ oldPassword: "", newPassword: "" });
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);

  useEffect(() => {
    const loadProfile = async () => {
      setLoading(true);
      setError("");

      try {
        if (isPatient) {
          setProfile(await getPatientByUserId(user.userId));
        } else if (isDoctor) {
          setProfile(await getDoctorByUserId(user.userId));
        }

        try {
          const savedAddress = await getAddressByUserId(user.userId);
          if (savedAddress) setAddress({ ...EMPTY_ADDRESS, ...savedAddress });
        } catch (addressError) {
          // A user may not have an address yet. Keep the form empty so it can be added.
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, [isPatient, isDoctor, user.userId]);

  const openDetails = () => {
    setFormError("");
    setSuccess("");
    setDetailsOpen(true);
  };

  const openAddress = () => {
    setFormError("");
    setSuccess("");
    setAddressOpen(true);
  };

  const openPassword = () => {
    setFormError("");
    setSuccess("");
    setPasswords({ oldPassword: "", newPassword: "" });
    setPasswordOpen(true);
  };

  const updateDetails = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    setSuccess("");

    try {
      if (isPatient) {
        const updated = await updatePatient(profile.patientId, {
          firstName: profile.firstName,
          lastName: profile.lastName,
          gender: profile.gender,
          dateOfBirth: profile.dateOfBirth,
          phoneNumber: profile.phoneNumber,
        });
        setProfile(updated || profile);
      } else {
        const updated = await updateDoctor(profile.doctorId, {
          firstName: profile.firstName,
          lastName: profile.lastName,
          department: profile.department,
          qualification: profile.qualification,
          phoneNumber: profile.phoneNumber,
          consultationFee: Number(profile.consultationFee),
        });
        setProfile(updated || profile);
      }

      setDetailsOpen(false);
      setSuccess("Your details were updated successfully.");
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const updateAddress = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    setSuccess("");

    try {
      const updated = await saveOrUpdateAddress(user.userId, address);
      setAddress({ ...address, ...(updated || {}) });
      setAddressOpen(false);
      setSuccess("Your address was saved successfully.");
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const updatePassword = async (e) => {
    e.preventDefault();
    setSaving(true);
    setFormError("");
    setSuccess("");

    try {
      await changePassword(user.userId, passwords.oldPassword, passwords.newPassword);
      setPasswordOpen(false);
      setPasswords({ oldPassword: "", newPassword: "" });
      setSuccess("Password changed successfully.");
    } catch (err) {
      setFormError(err.message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Loader />;

  if (!isPatient && !isDoctor) {
    return (
      <div>
        <h4 className="mb-3">My Profile</h4>
        <div className="alert alert-info">
          Profile editing is currently available for doctors and patients.
        </div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div>
        <h4 className="mb-3">My Profile</h4>
        <div className="alert alert-danger">{error || "Profile could not be loaded."}</div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 900 }}>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <div>
          <h4 className="mb-1">My Profile</h4>
          <span className="text-muted">User #{user.userId}</span>
        </div>
        <div className="d-flex gap-2">
          <button type="button" className="btn btn-outline-primary" onClick={openDetails}>
            <i className="bi bi-pencil me-1"></i>Update details
          </button>
          <button type="button" className="btn btn-outline-primary" onClick={openAddress}>
            <i className="bi bi-geo-alt me-1"></i>Address
          </button>
          <button type="button" className="btn btn-outline-secondary" onClick={openPassword}>
            <i className="bi bi-key me-1"></i>Change password
          </button>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {success && <div className="alert alert-success">{success}</div>}

      <div className="card border-0 shadow-sm mb-3">
        <div className="card-header bg-white">Personal details</div>
        <div className="card-body">
          <div className="row g-3">
            <div className="col-md-6"><span className="text-muted">Name:</span> {profile.firstName} {profile.lastName}</div>
            {isPatient ? (
              <>
                <div className="col-md-6"><span className="text-muted">Gender:</span> {profile.gender}</div>
                <div className="col-md-6"><span className="text-muted">Date of birth:</span> {profile.dateOfBirth}</div>
                <div className="col-md-6"><span className="text-muted">Phone:</span> {profile.phoneNumber}</div>
                <div className="col-md-6"><span className="text-muted">Patient ID:</span> {profile.patientId}</div>
              </>
            ) : (
              <>
                <div className="col-md-6"><span className="text-muted">Department:</span> {departmentLabel(profile.department)}</div>
                <div className="col-md-6"><span className="text-muted">Qualification:</span> {profile.qualification}</div>
                <div className="col-md-6"><span className="text-muted">Phone:</span> {profile.phoneNumber}</div>
                <div className="col-md-6"><span className="text-muted">Consultation fee:</span> ₹{Number(profile.consultationFee).toFixed(2)}</div>
                <div className="col-md-6"><span className="text-muted">Doctor ID:</span> {profile.doctorId}</div>
              </>
            )}
          </div>
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-header bg-white">Address</div>
        <div className="card-body">
          <div className="row g-3">
            <div className="col-md-4"><span className="text-muted">Street:</span> {address.street || "Not added"}</div>
            <div className="col-md-4"><span className="text-muted">City:</span> {address.city || "Not added"}</div>
            <div className="col-md-4"><span className="text-muted">State:</span> {address.state || "Not added"}</div>
          </div>
        </div>
      </div>

      {detailsOpen && (
        <Modal title="Update details" size="modal-lg" onClose={() => setDetailsOpen(false)}>
          <form onSubmit={updateDetails}>
            <div className="row g-3">
              <Field label="First name" required>
                <input className="form-control" value={profile.firstName} onChange={(e) => setProfile({ ...profile, firstName: e.target.value })} required />
              </Field>
              <Field label="Last name" required>
                <input className="form-control" value={profile.lastName} onChange={(e) => setProfile({ ...profile, lastName: e.target.value })} required />
              </Field>

              {isPatient ? (
                <>
                  <Field label="Gender" required>
                    <select className="form-select" value={profile.gender} onChange={(e) => setProfile({ ...profile, gender: e.target.value })} required>
                      {GENDERS.map((g) => <option key={g} value={g}>{g.charAt(0) + g.slice(1).toLowerCase()}</option>)}
                    </select>
                  </Field>
                  <Field label="Date of birth" required>
                    <input type="date" max={todayStr()} className="form-control" value={profile.dateOfBirth} onChange={(e) => setProfile({ ...profile, dateOfBirth: e.target.value })} required />
                  </Field>
                  <Field label="Phone" required>
                    <input className="form-control" value={profile.phoneNumber} onChange={(e) => setProfile({ ...profile, phoneNumber: e.target.value })} required />
                  </Field>
                </>
              ) : (
                <>
                  <Field label="Department" required>
                    <select className="form-select" value={profile.department} onChange={(e) => setProfile({ ...profile, department: e.target.value })} required>
                      {DEPARTMENTS.map((d) => <option key={d} value={d}>{departmentLabel(d)}</option>)}
                    </select>
                  </Field>
                  <Field label="Qualification" required>
                    <input className="form-control" value={profile.qualification} onChange={(e) => setProfile({ ...profile, qualification: e.target.value })} required />
                  </Field>
                  <Field label="Phone" required>
                    <input className="form-control" value={profile.phoneNumber} onChange={(e) => setProfile({ ...profile, phoneNumber: e.target.value })} required />
                  </Field>
                  <Field label="Consultation fee" required>
                    <input type="number" min="0" step="0.01" className="form-control" value={profile.consultationFee} onChange={(e) => setProfile({ ...profile, consultationFee: e.target.value })} required />
                  </Field>
                </>
              )}
            </div>
            {formError && <div className="alert alert-danger mt-3 mb-0">{formError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setDetailsOpen(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Saving..." : "Save"}</button>
            </div>
          </form>
        </Modal>
      )}

      {addressOpen && (
        <Modal title="Add or update address" onClose={() => setAddressOpen(false)}>
          <form onSubmit={updateAddress}>
            <div className="row g-3">
              <Field label="Street" col="col-12" required>
                <input className="form-control" value={address.street} onChange={(e) => setAddress({ ...address, street: e.target.value })} required maxLength={100} />
              </Field>
              <Field label="City" required>
                <input className="form-control" value={address.city} onChange={(e) => setAddress({ ...address, city: e.target.value })} required maxLength={50} />
              </Field>
              <Field label="State" required>
                <input className="form-control" value={address.state} onChange={(e) => setAddress({ ...address, state: e.target.value })} required maxLength={50} />
              </Field>
            </div>
            {formError && <div className="alert alert-danger mt-3 mb-0">{formError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setAddressOpen(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Saving..." : "Save address"}</button>
            </div>
          </form>
        </Modal>
      )}

      {passwordOpen && (
        <Modal title="Change password" onClose={() => setPasswordOpen(false)}>
          <form onSubmit={updatePassword}>
            <Field label="Current password" col="col-12" required>
              <div className="input-group">
                <input type={showOldPassword ? "text" : "password"} className="form-control" value={passwords.oldPassword} onChange={(e) => setPasswords({ ...passwords, oldPassword: e.target.value })} required />
                <button type="button" className="btn btn-outline-secondary" onClick={() => setShowOldPassword((v) => !v)} aria-label={showOldPassword ? "Hide current password" : "Show current password"}>
                  <i className={`bi ${showOldPassword ? "bi-eye-slash" : "bi-eye"}`}></i>
                </button>
              </div>
            </Field>
            <div className="mt-3">
              <Field label="New password" col="col-12" required>
                <div className="input-group">
                  <input type={showNewPassword ? "text" : "password"} className="form-control" value={passwords.newPassword} onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })} required minLength={8} />
                  <button type="button" className="btn btn-outline-secondary" onClick={() => setShowNewPassword((v) => !v)} aria-label={showNewPassword ? "Hide new password" : "Show new password"}>
                    <i className={`bi ${showNewPassword ? "bi-eye-slash" : "bi-eye"}`}></i>
                  </button>
                </div>
              </Field>
            </div>
            {formError && <div className="alert alert-danger mt-3 mb-0">{formError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setPasswordOpen(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={saving}>{saving ? "Changing..." : "Change password"}</button>
            </div>
          </form>
        </Modal>
      )}
    </div>
  );
}
