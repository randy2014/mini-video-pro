import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, InputNumber, Select, Tag, App } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getBatches, createBatch } from '../../services/entitlement';
import { getProducts } from '../../services/entitlement';
import type { EntitlementBatch, EntitlementProduct } from '../../types/api';

export default function Batches() {
  const [data, setData] = useState<EntitlementBatch[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [products, setProducts] = useState<EntitlementProduct[]>([]);
  const [form] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async (p: number) => {
    setLoading(true);
    try {
      const res = await getBatches({ page: p, size: 20 });
      setData(res.records);
      setTotal(res.total);
    } finally { setLoading(false); }
  };
  useEffect(() => { fetchData(page); }, [page]);
  useEffect(() => { getProducts({ page: 1, size: 100 }).then(r => setProducts(r.records)).catch(() => {}); }, []);

  const handleCreate = async (values: any) => {
    await createBatch(values);
    message.success('批次创建成功，权益码已生成');
    setModalOpen(false);
    form.resetFields();
    fetchData(page);
  };

  const columns = [
    { title: '批次号', dataIndex: 'batchNo', key: 'batchNo' },
    { title: '产品ID', dataIndex: 'productId', key: 'productId', width: 80 },
    { title: '渠道', dataIndex: 'channelCode', key: 'channelCode' },
    { title: '总量', dataIndex: 'quantity', key: 'quantity', width: 80 },
    { title: '已生成', dataIndex: 'generatedCount', key: 'generatedCount', width: 80 },
    { title: '已激活', dataIndex: 'activatedCount', key: 'activatedCount', width: 80 },
    { title: '状态', dataIndex: 'status', key: 'status', width: 80, render: (v: string) => (
      <Tag color={v === 'ACTIVE' ? 'green' : v === 'CREATED' ? 'blue' : 'default'}>{v}</Tag>
    )},
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 170 },
  ];

  return (
    <Card title="权益码批次管理" extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建批次</Button>}>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
        pagination={{ current: page, total, pageSize: 20, onChange: setPage }} />
      <Modal title="新建权益码批次" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="productId" label="关联产品" rules={[{ required: true }]}>
            <Select placeholder="选择产品" options={products.map(p => ({ label: `${p.productName} (${p.productCode})`, value: p.id }))} />
          </Form.Item>
          <Form.Item name="channelCode" label="渠道编码"><Input /></Form.Item>
          <Form.Item name="quantity" label="生成数量" rules={[{ required: true }]}>
            <InputNumber min={1} max={100000} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
