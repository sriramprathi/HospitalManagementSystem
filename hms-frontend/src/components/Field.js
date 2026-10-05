// label + input wrapper used in all the forms
export default function Field({ label, col = "col-md-6", children }) {
  return (
    <div className={col}>
      <label className="form-label">{label}</label>
      {children}
    </div>
  );
}
