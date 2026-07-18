import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, Space, Tag, App, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { getPlatforms, createPlatform, updatePlatform, deletePlatform } from '../../services/platform';
import type { VideoPlatform } from '../../types/api';

const platformTypes = [
  { label: '视频网站', value: 'video' },
  { label: '音乐平台', value: 'music' },
  { label: '电视直播', value: 'tv' },
  { label: '影视剧', value: 'drama' },
];

const typeTagColors: Record<string, string> = {
  video: 'blue', music: 'green', tv: 'orange', drama: 'purple',
};

const typeLabels: Record<string, string> = {
  video: '视频网站', music: '音乐平台', tv: '电视直播', drama: '影视剧',
};

export default function PlatformManage() {
  const [data, setData] = useState<VideoPlatform[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [form] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async () => {
    setLoading(true);
    try { setData(await getPlatforms()); } finally { setLoading(false); }
  };
  useEffect(() => { fetchData(); }, []);

  const handleSubmit = async (values: any) => {
    const req = { ...values, platformType: values.platformType || 'video' };
    if (editingId) {
      await updatePlatform(editingId, req);
      message.success('平台更新成功');
    } else {
      await createPlatform(req);
      message.success('平台创建成功');
    }
    setModalOpen(false); setEditingId(null); form.resetFields(); fetchData();
  };

  const handleEdit = (record: VideoPlatform) => {
    setEditingId(record.id);
    form.setFieldsValue(record);
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    await deletePlatform(id);
    message.success('平台已删除');
    fetchData();
  };

  const columns = [
    { title: '平台编码', dataIndex: 'platformCode', key: 'platformCode', width: 100 },
    { title: '平台名称', dataIndex: 'platformName', key: 'platformName', width: 120 },
    {
      title: '类型', dataIndex: 'platformType', key: 'platformType', width: 100,
      render: (v: string) => <Tag color={typeTagColors[v] || 'default'}>{typeLabels[v] || v || '视频网站'}</Tag>,
    },
    { title: '首页URL', dataIndex: 'homeUrl', key: 'homeUrl', ellipsis: true },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (v: string) => <Tag color={v === 'ACTIVE' ? 'green' : 'orange'}>{v === 'ACTIVE' ? '正常' : v}</Tag>,
    },
    {
      title: '操作', key: 'actions', width: 160,
      render: (_: any, record: VideoPlatform) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确定删除此平台？" onConfirm={() => handleDelete(record.id)} okText="删除" cancelText="取消">
            <Button size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <Card title="平台管理" extra={
      <Button type="primary" icon={<PlusOutlined />} onClick={() => { setEditingId(null); form.resetFields(); setModalOpen(true); }}>
        新建平台
      </Button>
    }>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />

      <Modal title={editingId ? '编辑平台' : '新建平台'} open={modalOpen} onCancel={() => { setModalOpen(false); setEditingId(null); }}
        onOk={() => form.submit()} destroyOnClose>
        <Form form={form} layout="vertical" onFinish={handleSubmit}>
          <Form.Item name="platformCode" label="平台编码" rules={[{ required: true }]}>
            <Input disabled={!!editingId} />
          </Form.Item>
          <Form.Item name="platformName" label="平台名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="platformType" label="平台类型" rules={[{ required: true }]} initialValue="video">
            <Select options={platformTypes} />
          </Form.Item>
          <Form.Item name="homeUrl" label="首页URL" rules={[{ required: true }, { type: 'url' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="domains" label="域名（逗号分隔）">
            <Input placeholder="example.com, www.example.com" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
