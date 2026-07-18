import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, Space, Tag, App } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getAdminUsers, createAdmin, getRoles } from '../../services/admin';
import type { AdminVO, AdminRole } from '../../types/api';

export default function AdminUsers() {
  const [data, setData] = useState<AdminVO[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [roles, setRoles] = useState<AdminRole[]>([]);
  const [form] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async (p: number) => {
    setLoading(true);
    try {
      const res = await getAdminUsers({ page: p, size: 20 });
      setData(res.records);
      setTotal(res.total);
    } finally { setLoading(false); }
  };

  useEffect(() => { fetchData(page); }, [page]);
  useEffect(() => { getRoles().then(setRoles).catch(() => {}); }, []);

  const handleCreate = async (values: any) => {
    await createAdmin(values);
    message.success('创建成功');
    setModalOpen(false);
    form.resetFields();
    fetchData(page);
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '状态', dataIndex: 'status', key: 'status', render: (v: string) => (
      <Tag color={v === 'ACTIVE' ? 'green' : 'red'}>{v === 'ACTIVE' ? '正常' : '停用'}</Tag>
    )},
    { title: '最后登录', dataIndex: 'lastLoginAt', key: 'lastLoginAt', width: 180 },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 180 },
  ];

  return (
    <Card title="管理员管理" extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建管理员</Button>}>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
        pagination={{ current: page, total, pageSize: 20, onChange: setPage }} />
      <Modal title="新建管理员" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true, min: 6 }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="roleCodes" label="角色">
            <Select mode="multiple" placeholder="选择角色" options={roles.map(r => ({ label: r.roleName, value: r.roleCode }))} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
