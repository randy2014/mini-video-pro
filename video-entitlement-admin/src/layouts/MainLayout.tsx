import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Button, Dropdown, theme } from 'antd';
import {
  DashboardOutlined, UserOutlined, GiftOutlined, GlobalOutlined,
  PlayCircleOutlined, SafetyOutlined, SettingOutlined, LogoutOutlined,
  MenuFoldOutlined, MenuUnfoldOutlined, AppstoreOutlined,
  NodeIndexOutlined, ApartmentOutlined
} from '@ant-design/icons';
import { useAuthStore } from '../stores/auth';

const { Header, Sider, Content } = Layout;

const menuItems = [
  { key: '/', icon: <DashboardOutlined />, label: '首页' },
  { key: 'admin', icon: <UserOutlined />, label: '管理员管理', children: [
    { key: '/admin/users', label: '管理员列表' },
  ]},
  { key: 'entitlement', icon: <GiftOutlined />, label: '权益管理', children: [
    { key: '/entitlement/products', label: '产品管理' },
    { key: '/entitlement/batches', label: '批次管理' },
  ]},
  { key: 'platform', icon: <GlobalOutlined />, label: '平台管理', children: [
    { key: '/platform', label: '平台配置' },
  ]},
  { key: 'playback', icon: <PlayCircleOutlined />, label: '播放路由', children: [
    { key: '/playback/providers', label: '供应商管理' },
    { key: '/playback/routes', label: '线路管理' },
    { key: '/playback/rules', label: '规则管理' },
  ]},
  { key: 'risk', icon: <SafetyOutlined />, label: '风控管理' },
  { key: 'config', icon: <SettingOutlined />, label: '配置发布' },
];

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { username, clearAuth } = useAuthStore();
  const { token: { colorBgContainer } } = theme.useToken();

  const handleLogout = () => { clearAuth(); navigate('/login'); };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider trigger={null} collapsible collapsed={collapsed} style={{ background: colorBgContainer }}>
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center', borderBottom: '1px solid #f0f0f0' }}>
          <h2 style={{ color: '#1677ff', margin: 0, fontSize: collapsed ? 14 : 18, whiteSpace: 'nowrap' }}>
            {collapsed ? '权益' : '视频权益管理'}
          </h2>
        </div>
        <Menu mode="inline" selectedKeys={[location.pathname]} defaultOpenKeys={['admin', 'entitlement', 'platform', 'playback']}
          items={menuItems} onClick={({ key }) => navigate(key)} style={{ borderRight: 0 }} />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', background: colorBgContainer, display: 'flex', alignItems: 'center', justifyContent: 'space-between', boxShadow: '0 1px 4px rgba(0,0,0,0.08)' }}>
          <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)} style={{ fontSize: 16 }} />
          <Dropdown menu={{ items: [{ key: 'logout', icon: <LogoutOutlined />, label: '退出登录', onClick: handleLogout }] }}>
            <span style={{ cursor: 'pointer' }}><UserOutlined /> {username || '管理员'}</span>
          </Dropdown>
        </Header>
        <Content style={{ margin: 24, padding: 24, background: colorBgContainer, borderRadius: 8, minHeight: 280 }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
