import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, InputNumber, Select, Space, Tag, App } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getRules, createRule } from '../../services/playback';
import type { PlaybackRule } from '../../types/api';

export default function RuleManage() {
  const [data, setData] = useState<PlaybackRule[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [platformCode, setPlatformCode] = useState('tencent');
  const [form] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async () => { setLoading(true); try { setData(await getRules(platformCode)); } finally { setLoading(false); } };
  useEffect(() => { fetchData(); }, [platformCode]);

  const handleCreate = async (values: any) => {
    await createRule({ ...values, platformCode });
    message.success('创建成功');
    setModalOpen(false); form.resetFields(); fetchData();
  };

  const columns = [
    { title: '平台', dataIndex: 'platformCode', key: 'platformCode' },
    { title: '客户端', dataIndex: 'clientType', key: 'clientType', render: (v: string) => v || '全部' },
    { title: '线路组ID', dataIndex: 'routeGroupId', key: 'routeGroupId' },
    { title: '优先级', dataIndex: 'priority', key: 'priority', width: 80 },
    { title: '启用', dataIndex: 'enabled', key: 'enabled', width: 60, render: (v: boolean) => <Tag color={v ? 'green' : 'red'}>{v ? '是' : '否'}</Tag> },
  ];

  return (
    <Card title="路由规则管理" extra={
      <Space>
        <Input value={platformCode} onChange={(e) => setPlatformCode(e.target.value)} style={{ width: 160 }} addonBefore="平台" />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建规则</Button>
      </Space>
    }>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />
      <Modal title="新建路由规则" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="clientType" label="客户端类型">
            <Select allowClear placeholder="全部" options={[
              { label: 'Android', value: 'ANDROID' }, { label: 'iOS', value: 'IOS' }, { label: 'H5', value: 'H5' },
            ]} />
          </Form.Item>
          <Form.Item name="routeGroupId" label="线路组ID" rules={[{ required: true }]}><InputNumber min={1} style={{ width: '100%' }} /></Form.Item>
          <Form.Item name="priority" label="优先级" initialValue={0}><InputNumber min={0} style={{ width: '100%' }} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
