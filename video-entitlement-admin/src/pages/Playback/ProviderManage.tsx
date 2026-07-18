import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, Tag, App } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getProviders, createProvider } from '../../services/playback';
import type { PlaybackProvider } from '../../types/api';

export default function ProviderManage() {
  const [data, setData] = useState<PlaybackProvider[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async () => { setLoading(true); try { setData(await getProviders()); } finally { setLoading(false); } };
  useEffect(() => { fetchData(); }, []);

  const handleCreate = async (values: any) => {
    await createProvider(values);
    message.success('创建成功');
    setModalOpen(false); form.resetFields(); fetchData();
  };

  const columns = [
    { title: '供应商编码', dataIndex: 'providerCode', key: 'providerCode' },
    { title: '供应商名称', dataIndex: 'providerName', key: 'providerName' },
    { title: '类型', dataIndex: 'providerType', key: 'providerType', render: (v: string) => {
      const map: Record<string, string> = { OFFICIAL_PLATFORM: '官方平台', AUTHORIZED_PARTNER: '授权合作', CLOUD_VOD: '云点播', INTERNAL: '自有内容', MOCK: '测试' };
      return map[v] || v;
    }},
    { title: '状态', dataIndex: 'status', key: 'status', render: (v: string) => <Tag color={v === 'ACTIVE' ? 'green' : 'orange'}>{v}</Tag> },
    { title: '授权状态', dataIndex: 'authorizationStatus', key: 'authorizationStatus', render: (v: string) => (
      <Tag color={v === 'VERIFIED' ? 'green' : v === 'PENDING' ? 'blue' : 'red'}>{v === 'VERIFIED' ? '已验证' : v === 'PENDING' ? '待审核' : v}</Tag>
    )},
  ];

  return (
    <Card title="供应商管理" extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建供应商</Button>}>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />
      <Modal title="新建供应商" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="providerCode" label="供应商编码" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="providerName" label="供应商名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="providerType" label="供应商类型" rules={[{ required: true }]}>
            <Select options={[
              { label: '官方视频平台', value: 'OFFICIAL_PLATFORM' },
              { label: '授权合作供应商', value: 'AUTHORIZED_PARTNER' },
              { label: '云点播供应商', value: 'CLOUD_VOD' },
              { label: '项目自有内容', value: 'INTERNAL' },
              { label: '开发测试供应商', value: 'MOCK' },
            ]} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
