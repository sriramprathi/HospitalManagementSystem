import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Patients from "./pages/Patients";
import Doctors from "./pages/Doctors";
import Appointments from "./pages/Appointments";
import BookAppointment from "./pages/BookAppointment";
import Prescriptions from "./pages/Prescriptions";
import { PATHS } from "./config/roles";
import Medicines from "./pages/Medicines";
import Bills from "./pages/Bills";
import Staff from "./pages/Staff";
import Profile from "./pages/Profile";



export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          {/* every page below gets the Navbar + Sidebar, and is protected by role */}
          <Route
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route path={PATHS.dashboard} element={<Dashboard />} />
            <Route path={PATHS.patients} element={<Patients />} />
            <Route path={PATHS.doctors} element={<Doctors />} />
            <Route path={PATHS.appointments} element={<Appointments />} />
            <Route path={PATHS.bookAppointment} element={<BookAppointment />} />
            <Route path={PATHS.prescriptions} element={<Prescriptions />} />
            <Route path={PATHS.bills} element={<Bills />} />
            <Route path={PATHS.medicines} element={<Medicines />} />
            <Route path={PATHS.staff} element={<Staff />} />
            <Route path={PATHS.profile} element={<Profile />} />
          </Route>

          <Route path="*" element={<Navigate to={PATHS.dashboard} replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
