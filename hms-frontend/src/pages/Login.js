import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import {
  addPatient,
  getDoctorByUserId,
  getPatientByUserId,
  login,
} from "../services/ApiService";
import { useAuth } from "../context/AuthContext";
import Modal from "../components/Modal";
import Field from "../components/Field";
import { GENDERS, todayStr } from "../config/constants";
import { PATHS } from "../config/roles";

const EMPTY_PATIENT = {
  firstName: "",
  lastName: "",
  gender: "Male",
  dateOfBirth: "",
  phoneNumber: "",
};

export default function Login() {
  const { user, signIn } = useAuth();
  const navigate = useNavigate();

  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // new patient registration
  const [showRegister, setShowRegister] = useState(false);
  const [regValues, setRegValues] = useState(EMPTY_PATIENT);
  const [regError, setRegError] = useState("");
  const [regSaving, setRegSaving] = useState(false);
  const [creds, setCreds] = useState(null); // login details shown once after registering

  if (user) return <Navigate to={PATHS.dashboard} replace />;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const data = await login(userName.trim(), password);

      let profileId;

      try {
        const role = data.role.toUpperCase();
        if (role === "PATIENT") {
          profileId = (await getPatientByUserId(data.userId)).patientId;
        }
        
        if (role === "DOCTOR") {
          profileId = (await getDoctorByUserId(data.userId)).doctorId;
        }
      } catch (e2) {
        // Login still works without a linked profile.
      }

      signIn({ ...data, profileId });
      navigate(PATHS.dashboard, { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const openRegister = () => {
    setRegError("");
    setRegValues(EMPTY_PATIENT);
    setShowRegister(true);
  };

  const setReg = (key) => (e) => setRegValues({ ...regValues, [key]: e.target.value });

  const handleRegister = async (e) => {
    e.preventDefault();
    setRegError("");
    setRegSaving(true);
    try {
      setCreds(await addPatient(regValues));
      setShowRegister(false);
    } catch (err) {
      setRegError(err.message);
    } finally {
      setRegSaving(false);
    }
  };

  // after registering, fill in the new username so the patient only has to type the password
  const closeCreds = () => {
    setUserName(creds.userName);
    setPassword("");
    setCreds(null);
  };

  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center bg-white p-4">
      <div className="login-card">
        <div className="text-center mb-4">
          <span
            className="brand-mark"
            style={{ width: 48, height: 48, fontSize: "1.5rem" }}
          >
            <i className="bi bi-hospital"></i>
          </span>
          <h4 className="fw-bold mt-3 mb-1">Hospital Management</h4>
          <p className="text-muted mb-0">Sign in to continue</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label" htmlFor="userName">
              Username<span className="text-danger ms-1">*</span>
            </label>
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-person"></i>
              </span>
              <input
                id="userName"
                className="form-control"
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                required
                autoFocus
              />
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label" htmlFor="password">
              Password<span className="text-danger ms-1">*</span>
            </label>
            <div className="input-group">
              <span className="input-group-text">
                <i className="bi bi-lock"></i>
              </span>
              <input
                id="password"
                type={showPassword ? "text" : "password"}
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <button
                type="button"
                className="btn btn-outline-secondary password-toggle"
                onClick={() => setShowPassword((value) => !value)}
                aria-label={showPassword ? "Hide password" : "Show password"}
                title={showPassword ? "Hide password" : "Show password"}
              >
                <i className={`bi ${showPassword ? "bi-eye-slash" : "bi-eye"}`}></i>
              </button>
            </div>
          </div>

          {error && (
            <div className="alert alert-danger py-2 d-flex align-items-center">
              <i className="bi bi-exclamation-circle me-2"></i>
              {error}
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary w-100 py-2"
            disabled={loading}
          >
            {loading ? (
              <>
                <span
                  className="spinner-border spinner-border-sm me-2"
                  role="status"
                ></span>
                Signing in...
              </>
            ) : (
              "Sign in"
            )}
          </button>
        </form>

        <p className="text-center text-muted mt-3 mb-0">
          New patient?{" "}
          <button type="button" className="btn btn-link p-0 align-baseline" onClick={openRegister}>
            Register here
          </button>
        </p>
      </div>

      {showRegister && (
        <Modal title="Register as a new patient" size="modal-lg" onClose={() => setShowRegister(false)}>
          <form onSubmit={handleRegister}>
            <div className="row g-3">
              <Field label="First name" required>
                <input className="form-control" value={regValues.firstName} onChange={setReg("firstName")}
                  pattern="[A-Za-z]+" title="Letters only, no spaces" required />
              </Field>
              <Field label="Last name" required>
                <input className="form-control" value={regValues.lastName} onChange={setReg("lastName")}
                  pattern="[A-Za-z]+" title="Letters only, no spaces" required />
              </Field>
              <Field label="Gender" required>
                <select className="form-select" value={regValues.gender} onChange={setReg("gender")}>
                  {GENDERS.map((g) => <option key={g}>{g}</option>)}
                </select>
              </Field>
              <Field label="Date of birth" required>
                <input type="date" max={todayStr()} className="form-control" value={regValues.dateOfBirth}
                  onChange={setReg("dateOfBirth")} required />
              </Field>
              <Field label="Phone" required>
                <input className="form-control" value={regValues.phoneNumber} onChange={setReg("phoneNumber")}
                  pattern="[0-9]{10}" maxLength={10} title="Exactly 10 digits" required />
              </Field>
            </div>
            {regError && <div className="alert alert-danger mt-3 mb-0">{regError}</div>}
            <div className="d-flex justify-content-end gap-2 mt-3">
              <button type="button" className="btn btn-light" onClick={() => setShowRegister(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={regSaving}>
                {regSaving ? "Registering..." : "Register"}
              </button>
            </div>
          </form>
        </Modal>
      )}

      {creds && (
        <Modal title="Registration successful" onClose={closeCreds}>
          <p>Please note these login details. The password is shown only now.</p>
          <ul className="list-group mb-3">
            <li className="list-group-item"><b>Username:</b> {creds.userName}</li>
            <li className="list-group-item"><b>Password:</b> {creds.password}</li>
          </ul>
          <button className="btn btn-primary" onClick={closeCreds}>Go to sign in</button>
        </Modal>
      )}
    </div>
  );
}