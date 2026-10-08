const BASE_URL = "http://localhost:8888/api";

export async function request(path, options = {}) {
  let res;
  try {
    res = await fetch(`${BASE_URL}${path}`, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
  } catch (e) {
    throw new Error("Cannot reach the server. Please check that the backend is running.");
  }
 
  if (!res.ok) {
   
    let message = `Request failed (${res.status})`;
    const text = await res.text();
    if (text) {
      try {
        message = JSON.parse(text).message || message;
      } catch (e) {
        message = text;
      }
    }
    throw new Error(message);
  }
 
  return res.status === 204 ? null : res.json();
}



const send = (method, body) => ({
  method,
  body: JSON.stringify(body),
});

// ---------- Users ----------
export const login = (userName, password) =>
  request("/users/login", send("POST", { userName, password }));

  export const changePassword = (userId, oldPassword, newPassword) =>
  request(`/users/${userId}/password`, send("PUT", { oldPassword, newPassword }));
// ---------- Address ----------
export const getAddressByUserId = (userId) =>
  request(`/users/${userId}/address`);

// AddressController in the HMS project exposes the user-scoped address resource.
export const saveOrUpdateAddress = (userId, dto) =>
  request(`/users/${userId}/address`, send("PUT", dto));

// ---------- Patients ----------
export const getAllPatients = () => request("/patients");
export const getPatientByUserId = (userId) =>
  request(`/patients/user/${userId}`);
export const addPatient = (dto) =>
  request("/patients", send("POST", dto));
export const updatePatient = (id, dto) =>
  request(`/patients/${id}`, send("PUT", dto));
export const deletePatient = (id) =>
  request(`/patients/${id}`, { method: "DELETE" });

// ---------- Doctors ----------
export const getAllDoctors = () => request("/doctors");
export const getDoctorsByDepartment = (department) =>
  request(`/doctors/department/${department}`);
export const getDoctorByUserId = (userId) =>
  request(`/doctors/user/${userId}`);
export const addDoctor = (dto) =>
  request("/doctors", send("POST", dto));
export const updateDoctor = (id, dto) =>
  request(`/doctors/${id}`, send("PUT", dto));
export const deleteDoctor = (id) =>
  request(`/doctors/${id}`, { method: "DELETE" });

// ---------- Appointments ----------
export const bookAppointment = (dto) =>
  request("/appointments", send("POST", dto));
export const getAppointmentsByPatient = (id) =>
  request(`/appointments/patient/${id}`);
export const getAppointmentsByDoctor = (id) =>
  request(`/appointments/doctor/${id}`);
export const getAppointmentsByDate = (date) =>
  request(`/appointments/date?date=${date}`);
export const rescheduleAppointment = (id, dto) =>
  request(`/appointments/${id}/reschedule`, send("PUT", dto));
export const cancelAppointment = (id) =>
  request(`/appointments/${id}/cancel`, { method: "PATCH" });
export const updateAppointmentStatus = (id, status) =>
  request(`/appointments/${id}/status?status=${status}`, {
    method: "PATCH",
  });
  export const getAvailableSlots = (doctorId, date) =>
  request(`/appointments/doctor/${doctorId}/slots?date=${date}`);

// ---------- Prescriptions ----------
export const createPrescription = (appointmentId, dto) =>
  request(
    `/prescriptions/appointment/${appointmentId}`,
    send("POST", dto)
  );
export const getPrescriptionByAppointment = (appointmentId) =>
  request(`/prescriptions/appointment/${appointmentId}`);
export const getPrescriptionsByPatient = (patientId) =>
  request(`/prescriptions/patient/${patientId}`);
export const updatePrescription = (id, dto) =>
  request(`/prescriptions/${id}`, send("PUT", dto));
export const deletePrescription = (id) =>
  request(`/prescriptions/${id}`, { method: "DELETE" });

// ---------- Medicines ----------
export const getAllMedicines = () => request("/medicines");
export const searchMedicines = (name) =>
  request(`/medicines/search?name=${encodeURIComponent(name)}`);
export const addMedicine = (dto) =>
  request("/medicines", send("POST", dto));
export const updateMedicine = (id, dto) =>
  request(`/medicines/${id}`, send("PUT", dto));
export const deleteMedicine = (id) =>
  request(`/medicines/${id}`, { method: "DELETE" });

  // ---------- Bills ----------
export const getAllBills = () => request("/bills");
export const getBillsByPatient = (patientId) => request(`/bills/patient/${patientId}`);
export const generateConsultationBill = (appointmentId) =>
  request(`/bills/consultation/appointment/${appointmentId}`, { method: "POST" });
export const generateMedicineBill = (prescriptionId) =>
  request(`/bills/medicine/prescription/${prescriptionId}`, { method: "POST" });
export const updateBillStatus = (id, status) => request(`/bills/${id}/status?status=${status}`, { method: "PATCH" });

export const getUsersByRole = (role) => request(`/users/role/${role}`);
export const registerUser = (dto) => request("/users/register", send("POST", dto));
export const deleteUser = (userId) => request(`/users/${userId}`, { method: "DELETE" });