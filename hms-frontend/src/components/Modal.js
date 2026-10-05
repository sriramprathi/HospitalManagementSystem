// Bootstrap modal controlled by React (no Bootstrap JavaScript needed)
export default function Modal({ title, onClose, size = "", children }) {
  return (
    <>
      <div className="modal-backdrop fade show"></div>
      <div className="modal d-block" tabIndex="-1" onClick={onClose}>
        <div
          className={`modal-dialog modal-dialog-centered modal-dialog-scrollable ${size}`}
          onClick={(e) => e.stopPropagation()}
        >
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">{title}</h5>
              <button type="button" className="btn-close" onClick={onClose}></button>
            </div>
            <div className="modal-body">{children}</div>
          </div>
        </div>
      </div>
    </>
  );
}
