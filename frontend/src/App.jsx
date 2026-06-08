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
    <AppShell currentPage={currentPage} setCurrentPage={setCurrentPage}>
      <Routes>
        <Route
          path="/dashboard"
          element={
            <DashboardPage
              username={auth?.tokenParsed?.preferred_username || 'admin'}
            />
          }
        />

        <Route path="/leads" element={<LeadsPage />} />
        <Route path="/inbox" element={<InboxPage />} />
        <Route path="/pipeline" element={<PipelinePage />} />

        <Route path="/lcl" element={<LclPage />} />
        <Route path="/lcl/nueva" element={<LclPage mode="new" />} />
        <Route path="/lcl/:id" element={<LclPage mode="detail" />} />
        <Route path="/tasks" element={<TasksPage />} />

        <Route path="/account" element={<MyAccountPage />} />

        <Route
          path="/parameters"
          element={
            hasRole('ADMIN', 'GERENCIA')
              ? <ParametersPage />
              : <Navigate to="/dashboard" replace />
          }
        />

        <Route
          path="/admin/users"
          element={
            hasRole('ADMIN')
              ? <UsersAdminPage />
              : <Navigate to="/dashboard" replace />
          }
        />

        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />

        <Route
          path="/opportunities/:id"
          element={<Opportunity360Page />}
        />
      </Routes>
    </AppShell>
  );
}