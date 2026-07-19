import { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, DatePicker, Select, Space, Popconfirm, message } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { getEntitlements, createEntitlement, updateEntitlement, deleteEntitlement } from '../../services/entitlement';
import type { Entitlement, EntitlementRequest } from '../../types/api';
import dayjs from 'dayjs';

export default function EntitlementManage() {
  const [data, setData] = useState<Entitlement[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form] = Form.useForm();

  const fetch = (p = 0) => {
    setLoading(true);
    getEntitlements({ page: p, size: 10 })
      .then((res) => { setData(res.records); setTotal(res.total); setPage(res.page); })
      .catch(() => message.error('加载失败'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetch(); }, []);

  const openCreate = () => {
    setEditingId(null);
    form.resetFields();
    form.setFieldsValue({ status: 'DISABLED' });
    setModalOpen(true);
  };

  const openEdit = (record: Entitlement) => {
    setEditingId(record.id);
    form.setFieldsValue({
      ...record,
      startTime: record.startTime ? dayjs(record.startTime) : null,
      endTime: record.endTime ? dayjs(record.endTime) : null,
    });
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    const req: EntitlementRequest = {
      ...values,
      startTime: values.startTime ? values.startTime.format('YYYY-MM-DDTHH:mm:ss') : undefined,
      endTime: values.endTime ? values.endTime.format('YYYY-MM-DDTHH:mm:ss') : undefined,
    };
    if (editingId) {
      await updateEntitlement(editingId, req);
      message.success('编辑成功');
    } else {
      await createEntitlement(req);
      message.success('创建成功');
    }
    setModalOpen(false);
    fetch(page);
  };

  const handleDelete = async (id: number) => {
    await deleteEntitlement(id);
    message.success('已删除');
    fetch(page);
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '权益名称', dataIndex: 'entitlementName' },
    { title: '权益代码', dataIndex: 'entitlementCode', width: 100 },
    { title: '拥有人', dataIndex: 'ownerName', width: 100 },
    { title: '电话', dataIndex: 'ownerPhone', width: 120 },
    { title: '状态', dataIndex: 'status', width: 80, render: (s: string) => s === 'ENABLED' ? '✅ 启用' : '⛔ 禁用' },
    { title: '开始时间', dataIndex: 'startTime', width: 120 },
    { title: '结束时间', dataIndex: 'endTime', width: 120 },
    {
      title: '操作', width: 140,
      render: (_: unknown, record: Entitlement) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(record)} />
          <Popconfirm title="确认删除？" onConfirm={() => handleDelete(record.id)}>
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Button type="primary" icon={<PlusOutlined />} onClick={openCreate} style={{ marginBottom: 16 }}>新增权益</Button>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
        pagination={{ current: page + 1, total, pageSize: 10, onChange: (p) => fetch(p - 1) }} size="small" />

      <Modal title={editingId ? '编辑权益' : '新增权益'} open={modalOpen}
        onOk={handleSubmit} onCancel={() => setModalOpen(false)} destroyOnClose width={600}>
        <Form form={form} layout="vertical">
          <Form.Item name="entitlementName" label="权益名称" rules={[{ required: true, message: '请输入' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="entitlementCode" label="权益代码" rules={editingId ? [{ required: true }] : []}>
            <Input disabled placeholder={editingId ? '' : '保存后自动生成8位数字'} />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true }]}
            extra="启用需设置开始和结束时间">
            <Select options={[{ value: 'ENABLED', label: '启用' }, { value: 'DISABLED', label: '禁用' }]} />
          </Form.Item>
          <Form.Item name="startTime" label="开始时间"><DatePicker showTime style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="endTime" label="结束时间"><DatePicker showTime style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="ownerName" label="拥有人"><Input /></Form.Item>
          <Form.Item name="ownerPhone" label="拥有人电话"><Input /></Form.Item>
          <Form.Item name="ownerProfession" label="拥有人职业"><Input /></Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
