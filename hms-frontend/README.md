# HMS Frontend (React + Bootstrap)

## Run
1. Start the Spring Boot backend on http://localhost:8080
2. In this folder:
   npm install
   npm start
3. Open http://localhost:3000

The API address is set in src/services/ApiService.js (BASE_URL).

## Folders
- public/            index.html
- src/components/    Layout, Navbar, Sidebar, Modal, Field, Loader, ProtectedRoute, Placeholder
- src/config/        roles (menus + routes per role), constants (dropdown values, helpers)
- src/context/       AuthContext (login state)
- src/hooks/         useApi (data loading)
- src/pages/         Login, Dashboard, Patients, Doctors, Appointments, BookAppointment, Prescriptions
- src/services/      ApiService (all backend calls)
- src/index.css      theme (colours, sidebar, cards, tables)
