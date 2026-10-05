import { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { login, getPatientByUserId, getDoctorByUserId } from "../services/ApiService";
import { useAuth } from "../context/AuthContext";
import { PATHS } from "../config/roles";

export default function Login() {
  const { user, signIn } = useAuth();
  const navigate = useNavigate();

  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (user) return <Navigate to={PATHS.dashboard} replace />;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const data = await login(userName.trim(), password); // { userId, role }

      // a Patient / Doctor also needs their own patientId / doctorId for later calls
      let profileId;
      try {
        if (data.role === "Patient") profileId = (await getPatientByUserId(data.userId)).patientId;
        if (data.role === "Doctor") profileId = (await getDoctorByUserId(data.userId)).doctorId;
      } catch (e2) {
        // login still works without a profile
      }

      signIn({ ...data, profileId });
      navigate(PATHS.dashboard, { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center bg-white p-4">
      <div className="login-card">
        <div className="text-center mb-4">
          <span className="brand-mark" style={{ width: 48, height: 48, fontSize: "1.5rem" }}>
            <i className="bi bi-hospital"></i>
          </span>
          <h4 className="fw-bold mt-3 mb-1">Hospital Management</h4>
          <p className="text-muted mb-0">Sign in to continue</p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label" htmlFor="userName">Username</label>
            <div className="input-group">
              <span className="input-group-text"><i className="bi bi-person"></i></span>
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
            <label className="form-label" htmlFor="password">Password</label>
            <div className="input-group">
              <span className="input-group-text"><i className="bi bi-lock"></i></span>
              <input
                id="password"
                type="password"
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          {error && (
            <div className="alert alert-danger py-2 d-flex align-items-center">
              <i className="bi bi-exclamation-circle me-2"></i>{error}
            </div>
          )}

          <button type="submit" className="btn btn-primary w-100 py-2" disabled={loading}>
            {loading ? (
              <>
                <span className="spinner-border spinner-border-sm me-2" role="status"></span>Signing in...
              </>
            ) : (
              "Sign in"
            )}
          </button>
        </form>
      </div>
    </div>
  );
}