
export const PATHS = {
  dashboard: "/dashboard",
  patients: "/patients",
  doctors: "/doctors",
  appointments: "/appointments",
  bookAppointment: "/book-appointment",
  medicines: "/medicines",
  prescriptions: "/prescriptions",
  bills: "/bills",
};

export const ROLES = {
  Administrator: {
    menu: [
      { label: "Dashboard", path: PATHS.dashboard },
      { label: "Patients", path: PATHS.patients },
      { label: "Doctors", path: PATHS.doctors },
      { label: "Appointments", path: PATHS.appointments },
      { label: "Medicines", path: PATHS.medicines },
      { label: "Prescriptions", path: PATHS.prescriptions },
      { label: "Bills", path: PATHS.bills },
    ],
  },
  Doctor: {
    menu: [
      { label: "Dashboard", path: PATHS.dashboard },
      { label: "Appointments", path: PATHS.appointments },
      { label: "Prescriptions", path: PATHS.prescriptions },
      { label: "Patients", path: PATHS.patients },
    ],
  },
  Patient: {
    menu: [
      { label: "Dashboard", path: PATHS.dashboard },
      { label: "Book Appointment", path: PATHS.bookAppointment },
      { label: "My Appointments", path: PATHS.appointments },
      { label: "My Prescriptions", path: PATHS.prescriptions },
      { label: "My Bills", path: PATHS.bills },
    ],
  },
  Pharmacist: {
    menu: [
      { label: "Dashboard", path: PATHS.dashboard },
      { label: "Medicines", path: PATHS.medicines },
      { label: "Prescriptions", path: PATHS.prescriptions },
    ],
  },
  Receptionist: {
    menu: [
      { label: "Dashboard", path: PATHS.dashboard },
      { label: "Patients", path: PATHS.patients },
      { label: "Appointments", path: PATHS.appointments },
      { label: "Book Appointment", path: PATHS.bookAppointment },
      { label: "Bills", path: PATHS.bills },
    ],
  },
};

export function canAccess(role, path) {
  return ROLES[role]?.menu.some((item) => item.path === path) ?? false;
}