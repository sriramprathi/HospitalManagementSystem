// values must match the backend enums exactly
export const DEPARTMENTS = ["Cardiology", "GeneralMedicine", "Neurology", "Pediatrician", "Radiology"];
export const GENDERS = ["Female", "Male", "Other"];
export const PATIENT_TYPES = ["Inpatient", "Outpatient"];
export const BLOOD_TYPES = [
  "A_POSITIVE", "A_NEGATIVE", "B_POSITIVE", "B_NEGATIVE",
  "AB_POSITIVE", "AB_NEGATIVE", "O_POSITIVE", "O_NEGATIVE",
];
export const STATUS_COLOR = { Scheduled: "primary", Completed: "success", Cancelled: "secondary" };

// A_POSITIVE -> A+
export const bloodLabel = (b) => (b ? b.replace("_POSITIVE", "+").replace("_NEGATIVE", "-") : "");

// today as YYYY-MM-DD in local time (toISOString would use UTC)
export const todayStr = () => {
  const d = new Date();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${m}-${day}`;
};

export const hhmm = (t) => (t ? String(t).slice(0, 5) : "");

export const MEDICINE_CATEGORIES = ["CAPSULE", "INJECTION", "OINTMENT", "OTHER", "SYRUP", "TABLET"];
