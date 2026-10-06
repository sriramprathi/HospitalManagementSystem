// Values must match the backend enums exactly.
export const DEPARTMENTS = [
  "CARDIOLOGY",
  "GENERAL_MEDICINE",
  "NEUROLOGY",
  "PEDIATRICIAN",
  "RADIOLOGY",
];

export const GENDERS = ["FEMALE", "MALE", "OTHER"];

export const STATUS = ["SCHEDULED", "COMPLETED", "CANCELLED"];
export const STATUS_COLOR = {
  SCHEDULED: "primary",
  COMPLETED: "success",
  CANCELLED: "secondary",
};

export const MEDICINE_CATEGORIES = [
  "CAPSULE",
  "INJECTION",
  "OINTMENT",
  "OTHER",
  "SYRUP",
  "TABLET",
];

export const roleLabel = (value) =>
  value
    ? value
        .toLowerCase()
        .replace(/_/g, " ")
        .replace(/\b\w/g, (c) => c.toUpperCase())
    : "";

export const departmentLabel = (value) =>
  value
    ? value
        .toLowerCase()
        .replace(/_/g, " ")
        .replace(/\b\w/g, (c) => c.toUpperCase())
    : "";

export const statusLabel = (value) =>
  value ? value.charAt(0) + value.slice(1).toLowerCase() : "";

// A_POSITIVE -> A+
export const bloodLabel = (b) =>
  b ? b.replace("_POSITIVE", "+").replace("_NEGATIVE", "-") : "";

// today as YYYY-MM-DD in local time (toISOString would use UTC)
export const todayStr = () => {
  const d = new Date();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${m}-${day}`;
};

export const hhmm = (t) => (t ? String(t).slice(0, 5) : "");
