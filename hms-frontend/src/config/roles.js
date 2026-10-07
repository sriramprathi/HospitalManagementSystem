export const PATHS = {
  dashboard: "/dashboard",
  patients: "/patients",
  doctors: "/doctors",
  appointments: "/appointments",
  bookAppointment: "/book-appointment",
  medicines: "/medicines",
  prescriptions: "/prescriptions",
  bills: "/bills",
  staff: "/staff",
  profile: "/profile",
};

const dashboard = { label: "Dashboard", path: PATHS.dashboard, icon: "bi-speedometer2" };
const patients = { label: "Patients", path: PATHS.patients, icon: "bi-people" };
const doctors = { label: "Doctors", path: PATHS.doctors, icon: "bi-person-badge" };
const appointments = { label: "Appointments", path: PATHS.appointments, icon: "bi-calendar-check" };
const book = { label: "Book Appointment", path: PATHS.bookAppointment, icon: "bi-calendar-plus" };
const medicines = { label: "Medicines", path: PATHS.medicines, icon: "bi-capsule" };
const prescriptions = { label: "Prescriptions", path: PATHS.prescriptions, icon: "bi-file-medical" };
const bills = { label: "Bills", path: PATHS.bills, icon: "bi-receipt" };
const staff = { label: "Staff", path: PATHS.staff, icon: "bi-person-lines-fill" };   
const profile = { label: "My Profile", path: PATHS.profile, icon: "bi-person-circle" };

// Role names must match the backend Role enum exactly.
export const ROLES = {
  ADMINISTRATOR: { menu: [dashboard, patients, doctors,staff, appointments, medicines, prescriptions, bills] },
  DOCTOR: { menu: [dashboard, appointments, prescriptions, patients, profile] },
  PATIENT: {
    menu: [
      dashboard,
      book,
      { ...appointments, label: "My Appointments" },
      { ...prescriptions, label: "My Prescriptions" },
      { ...bills, label: "My Bills" },
      profile,
    ],
  },
  PHARMACIST: { menu: [dashboard, medicines, prescriptions] },
  RECEPTIONIST: { menu: [dashboard, patients, appointments, book, bills] },
};

export function canAccess(role, path) {
  return ROLES[role]?.menu.some((item) => item.path === path) ?? false;
}
