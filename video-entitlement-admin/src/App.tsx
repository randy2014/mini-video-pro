import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/Login';
import Dashboard from './pages/Dashboard';
import AdminUsers from './pages/Admin/AdminUsers';
import PlatformManage from './pages/Platform/PlatformManage';
import RiskManage from './pages/Risk/RiskManage';
import ConfigReleasePage from './pages/ConfigRelease/ConfigRelease';
import EntitlementManage from './pages/Entitlement/EntitlementManage';
import UserManage from './pages/User/UserManage';
import { useAuthStore } from './stores/auth';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const isLoggedIn = useAuthStore((s) => s.isLoggedIn());
  if (!isLoggedIn) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
        <Route index element={<Dashboard />} />
        <Route path="admin/users" element={<AdminUsers />} />
        <Route path="platform" element={<PlatformManage />} />
        <Route path="risk" element={<RiskManage />} />
        <Route path="config" element={<ConfigReleasePage />} />
        <Route path="entitlement" element={<EntitlementManage />} />
        <Route path="users" element={<UserManage />} />
      </Route>
    </Routes>
  );
}
