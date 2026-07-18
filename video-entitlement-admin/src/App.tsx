import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './layouts/MainLayout';
import LoginPage from './pages/Login';
import Dashboard from './pages/Dashboard';
import AdminUsers from './pages/Admin/AdminUsers';
import EntitlementProducts from './pages/Entitlement/Products';
import EntitlementBatches from './pages/Entitlement/Batches';
import PlatformManage from './pages/Platform/PlatformManage';
import ProviderManage from './pages/Playback/ProviderManage';
import RouteManage from './pages/Playback/RouteManage';
import RuleManage from './pages/Playback/RuleManage';
import RiskManage from './pages/Risk/RiskManage';
import ConfigReleasePage from './pages/ConfigRelease/ConfigRelease';
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
        <Route path="entitlement/products" element={<EntitlementProducts />} />
        <Route path="entitlement/batches" element={<EntitlementBatches />} />
        <Route path="platform" element={<PlatformManage />} />
        <Route path="playback/providers" element={<ProviderManage />} />
        <Route path="playback/routes" element={<RouteManage />} />
        <Route path="playback/rules" element={<RuleManage />} />
        <Route path="risk" element={<RiskManage />} />
        <Route path="config" element={<ConfigReleasePage />} />
      </Route>
    </Routes>
  );
}
