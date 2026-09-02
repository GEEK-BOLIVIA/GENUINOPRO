import { Routes, Route, Navigate, useLocation, useNavigate } from 'react-router-dom';

import { useAuth } from './security/AuthProvider';

import AppShell from './layout/AppShell';

import DashboardPage from './pages/DashboardPage';
import LeadsPage from './pages/LeadsPage';
import InboxPage from './pages/InboxPage';
import PipelinePage from './pages/PipelinePage';
import LclPage from './pages/LclPage';
import ParametersPage from './pages/ParametersPage';

import UsersAdminPage from './modules/admin/UsersAdminPage';
import MyAccountPage from './modules/account/MyAccountPage';

import TasksPage from './pages/TasksPage';

import Opportunity360Page from './pages/Opportunity360Page';

import FclPage from './pages/FclPage';

import HblPage from './pages/HblPage';

import AirPage from './pages/AirPage';




export default function App() {
  const auth = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const roles = auth?.tokenParsed?.realm_access?.roles || [];

  const hasRole = (...allowedRoles) =>
    allowedRoles.some((role) => roles.includes(role));

  const pathByPage = {
    DASHBOARD: '/dashboard',
    LEADS: '/leads',
    INBOX: '/inbox',
    PIPELINE: '/pipeline',
    LCL: '/lcl',
    PARAMETERS: '/parameters',
    ADMIN_USERS: '/admin/users',
    ACCOUNT: '/account',
    TASKS: '/tasks',
  };

  const currentPage =
    location.pathname.startsWith('/leads') ? 'LEADS' :
    location.pathname.startsWith('/inbox') ? 'INBOX' :
    location.pathname.startsWith('/pipeline') ? 'PIPELINE' :
    location.pathname.startsWith('/lcl') ? 'LCL' :
    location.pathname.startsWith('/fcl') ? 'LCL' :
    location.pathname.startsWith('/hbl') ? 'LCL' :
    location.pathname.startsWith('/air') ? 'LCL' :
    location.pathname.startsWith('/parameters') ? 'PARAMETERS' :
    location.pathname.startsWith('/admin/users') ? 'ADMIN_USERS' :
    location.pathname.startsWith('/tasks') ? 'TASKS' :
    location.pathname.startsWith('/account') ? 'ACCOUNT' :
    location.pathname.startsWith('/opportunities') ? 'PIPELINE':
    'DASHBOARD';

  function setCurrentPage(page) {
    navigate(pathByPage[page] || '/dashboard');
  }

return (
  <AppShell
    currentPage={currentPage}
    setCurrentPage={setCurrentPage}
  >
    <Routes>

      <Route
        path="/dashboard"
        element={
          <DashboardPage
            username={
              auth?.tokenParsed?.preferred_username ||
              'admin'
            }
          />
        }
      />

      <Route
        path="/leads"
        element={<LeadsPage />}
      />

      <Route
        path="/inbox"
        element={<InboxPage />}
      />

      <Route
        path="/pipeline"
        element={<PipelinePage />}
      />

      <Route
        path="/tasks"
        element={<TasksPage />}
      />

      <Route
        path="/account"
        element={<MyAccountPage />}
      />

      {/* =====================================================
          OPORTUNIDADES
      ====================================================== */}

      <Route
        path="/opportunities/:id"
        element={<Opportunity360Page />}
      />

      {/* =====================================================
          LCL
      ====================================================== */}

      <Route
        path="/lcl"
        element={<LclPage />}
      />

      <Route
        path="/lcl/nueva"
        element={<LclPage mode="new" />}
      />

      <Route
        path="/lcl/:id/editar"
        element={<LclPage mode="edit" />}
      />

      <Route
        path="/lcl/:id"
        element={<LclPage mode="detail" />}
      />

      {/* =====================================================
          FCL
      ====================================================== */}

      <Route
        path="/fcl"
        element={<FclPage />}
      />

      <Route
        path="/fcl/nueva"
        element={<FclPage mode="new" />}
      />

      <Route
        path="/fcl/editar/:id"
        element={<FclPage mode="edit" />}
      />

      <Route
        path="/fcl/:id"
        element={<FclPage mode="detail" />}
      />

      {/* =====================================================
          HBL
      ====================================================== */}

      <Route
        path="/hbl/nueva"
        element={<HblPage mode="new" />}
      />

      <Route
        path="/hbl/:id/editar"
        element={<HblPage mode="edit" />}
      />

      <Route
        path="/hbl/:id"
        element={<HblPage mode="detail" />}
      />

      {/* =====================================================
          AÉREO
      ====================================================== */}

      <Route
        path="/air/nueva"
        element={<AirPage mode="new" />}
      />

      <Route
        path="/air/:id/editar"
        element={<AirPage mode="edit" />}
      />

      <Route
        path="/air/:id"
        element={<AirPage mode="detail" />}
      />

      {/* =====================================================
          PARÁMETROS
      ====================================================== */}

      <Route
        path="/parameters"
        element={
          hasRole('ADMIN', 'GERENCIA')
            ? <ParametersPage />
            : (
              <Navigate
                to="/dashboard"
                replace
              />
            )
        }
      />

      {/* =====================================================
          ADMINISTRACIÓN
      ====================================================== */}

      <Route
        path="/admin/users"
        element={
          hasRole('ADMIN')
            ? <UsersAdminPage />
            : (
              <Navigate
                to="/dashboard"
                replace
              />
            )
        }
      />

      {/* =====================================================
          DEFAULT
      ====================================================== */}

      <Route
        path="/"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />

      <Route
        path="*"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />

    </Routes>
  </AppShell>
);
}