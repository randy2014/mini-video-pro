import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, InputNumber, Tag, App } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getReleases, createRelease } from '../../services/config';
import type { ConfigRelease } from '../../types/api';
import dayjs from 'dayjs';

export default function ConfigReleasePage() {
  const [data, setData] = useState<ConfigRelease[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const fmt = (s?: string) => s ? dayjs(s).format('YYYY-MM-DD HH:mm') : '-';

  const fetchData = async (p: number) => {
    setLoading(true);
    try { const r = await getReleases({ page: p, size: 20 }); setData(r.records); setTotal(r.total); } finally { setLoading(false); }
  };
  useEffect(() => { fetchData(page); }, [page]);

  const handleCreate = async (values: any) => {
    await createRelease(values);
    message.success('发布创建成功');
    setModalOpen(false); form.resetFields(); fetchData(page);
  };

  const columns = [
    { title: '发布号', dataIndex: 'releaseNo', key: 'releaseNo' },
    { title: '版本', dataIndex: 'configVersion', key: 'configVersion' },
    { title: '类型', dataIndex: 'releaseType', key: 'releaseType', render: (v: string) => {
      const map: Record<string, string> = { DRAFT: '草稿', TEST: '测试', GRAY: '灰度', OFFICIAL: '正式' };
      return map[v] || v;
    }},
    { title: '灰度比例', dataIndex: 'grayPercentage', key: 'grayPercentage', render: (v: number) => v ? `${v}%` : '-' },
    { title: '状态', dataIndex: 'status', key: 'status', render: (v: string) => (
      <Tag color={v === 'PUBLISHED' ? 'green' : v === 'DRAFT' ? 'blue' : v === 'ROLLED_BACK' ? 'red' : 'orange'}>{v}</Tag>
    )},
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '发布时间', dataIndex: 'publishedAt', key: 'publishedAt', width: 160, render: (s: string) => fmt(s) },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (s: string) => fmt(s) },
  ];

  return (
    <Card title="配置发布管理" extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建发布</Button>}>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
        pagination={{ current: page, total, pageSize: 20, onChange: setPage }} />
      <Modal title="新建配置发布" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="releaseType" label="发布类型" rules={[{ required: true }]}>
            <Select options={[
              { label: '灰度', value: 'GRAY' }, { label: '测试', value: 'TEST' }, { label: '正式', value: 'OFFICIAL' },
            ]} />
          </Form.Item>
          <Form.Item name="description" label="描述"><Input.TextArea rows={2} /></Form.Item>
          <Form.Item name="grayPercentage" label="灰度比例(%)" initialValue={0}><InputNumber min={0} max={100} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
