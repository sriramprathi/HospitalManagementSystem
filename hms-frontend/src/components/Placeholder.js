export default function Placeholder({ title, what }) {
  return (
    <div>
      <h4 className="mb-3">{title}</h4>
      <div className="alert alert-info">
        This page needs the <b>{what}</b> service and controller from the backend. Once those are written, this
        page gets built the same way as the others.
      </div>
    </div>
  );
}
