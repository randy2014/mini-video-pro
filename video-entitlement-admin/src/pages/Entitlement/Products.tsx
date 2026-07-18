import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, InputNumber, Select, Space, Tag, App } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getProducts, createProduct } from '../../services/entitlement';
import type { EntitlementProduct } from '../../types/api';

export default function Products() {
  const [data, setData] = useState<EntitlementProduct[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async (p: number) => {
    setLoading(true);
    try {
      const res = await getProducts({ page: p, size: 20 });
      setData(res.records);
      setTotal(res.total);
    } finally { setLoading(false); }
  };
  useEffect(() => { fetchData(page); }, [page]);

  const handleCreate = async (values: any) => {
    await createProduct(values);
    message.success('创建成功');
    setModalOpen(false);
    form.resetFields();
    fetchData(page);
  };

  const columns = [
    { title: '产品编码', dataIndex: 'productCode', key: 'productCode' },
    { title: '产品名称', dataIndex: 'productName', key: 'productName' },
    { title: '有效期类型', dataIndex: 'validityType', key: 'validityType', render: (v: string) => (
      v === 'AFTER_ACTIVATION' ? '激活后' : v === 'FIXED_PERIOD' ? '固定时段' : '永久'
    )},
    { title: '有效期(天)', dataIndex: 'validDays', key: 'validDays', width: 100 },
    { title: '日限制', dataIndex: 'dailyUsageLimit', key: 'dailyUsageLimit', width: 80 },
    { title: '总限制', dataIndex: 'totalUsageLimit', key: 'totalUsageLimit', width: 80 },
    { title: '设备限制', dataIndex: 'deviceLimit', key: 'deviceLimit', width: 80 },
    { title: '状态', dataIndex: 'status', key: 'status', width: 80, render: (v: string) => (
      <Tag color={v === 'ACTIVE' ? 'green' : v === 'DRAFT' ? 'blue' : 'default'}>
        {v === 'ACTIVE' ? '启用' : v === 'DRAFT' ? '草稿' : v}
      </Tag>
    )},
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 170 },
  ];

  return (
    <Card title="权益产品管理" extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建产品</Button>}>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
        pagination={{ current: page, total, pageSize: 20, onChange: setPage }} />
      <Modal title="新建权益产品" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()} width={600}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="productName" label="产品名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} /></Form.Item>
          <Space>
            <Form.Item name="validityType" label="有效期类型" rules={[{ required: true }]}>
              <Select style={{ width: 160 }} options={[
                { label: '激活后生效', value: 'AFTER_ACTIVATION' },
                { label: '固定时间段', value: 'FIXED_PERIOD' },
                { label: '永久有效', value: 'PERMANENT' },
              ]} />
            </Form.Item>
            <Form.Item name="validDays" label="有效天数"><InputNumber min={1} /></Form.Item>
          </Space>
          <Space>
            <Form.Item name="dailyUsageLimit" label="每日次数限制"><InputNumber min={1} /></Form.Item>
            <Form.Item name="totalUsageLimit" label="总次数限制"><InputNumber min={1} /></Form.Item>
            <Form.Item name="deviceLimit" label="设备数限制"><InputNumber min={1} /></Form.Item>
          </Space>
        </Form>
      </Modal>
    </Card>
  );
}
