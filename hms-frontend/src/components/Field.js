// Label + input wrapper used in all the forms.
export default function Field({ label, col = "col-md-6", required = false, children }) {
  return (
    <div className={col}>
      <label className="form-label">
        {label}
        {required && <span className="text-danger ms-1">*</span>}
      </label>
      {children}
    </div>
  );
}
