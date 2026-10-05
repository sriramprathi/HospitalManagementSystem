import { useState } from "react";
import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";

// Navbar on top, Sidebar on the left, the current page in the middle (<Outlet />)
export default function Layout() {
  const [open, setOpen] = useState(false);

  return (
    <div className="min-vh-100">
      <Navbar onToggleSidebar={() => setOpen((o) => !o)} />
      <div className="d-flex">
        <Sidebar open={open} onNavigate={() => setOpen(false)} />
        <main className="flex-grow-1 p-3 p-md-4" style={{ minWidth: 0 }}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
