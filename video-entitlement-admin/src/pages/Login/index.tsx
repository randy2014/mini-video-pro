import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Form, Input, Button, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { adminLogin } from '../../services/admin';
import { useAuthStore } from '../../stores/auth';

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);
  const [form] = Form.useForm();

  // 记住的账号密码自动填入
  useEffect(() => {
    const savedUser = localStorage.getItem('rememberedAdminUser');
    const savedPass = localStorage.getItem('rememberedAdminPass');
    if (savedUser) {
      form.setFieldsValue({ username: savedUser, password: savedPass || '' });
    }
  }, [form]);

  const handleSubmit = async (values: { username: string; password: string }) => {
    setLoading(true);
    try {
      const result = await adminLogin(values);
      setAuth(result.adminToken, result.username, result.permissions);
      // 记住账号密码
      localStorage.setItem('rememberedAdminUser', values.username);
      localStorage.setItem('rememberedAdminPass', values.password);
      message.success('登录成功');
      navigate('/');
    } catch {
      // error handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <Card className="login-card" title="视频权益管理后台">
        <Form form={form} onFinish={handleSubmit} size="large">
          <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
            <Input prefix={<UserOutlined />} placeholder="用户名" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
            <Input.Password prefix={<LockOutlined />} placeholder="密码" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>登录</Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
