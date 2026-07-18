import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, InputNumber, Select, Space, Tag, App } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getRoutes, createRoute, getRouteHealth } from '../../services/playback';
import type { PlaybackRoute, RouteHealth } from '../../types/api';

export default function RouteManage() {
  const [data, setData] = useState<PlaybackRoute[]>([]);
  const [health, setHealth] = useState<RouteHealth[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [groupId, setGroupId] = useState(1);
  const [form] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async () => {
    setLoading(true);
    try {
      setData(await getRoutes(groupId));
      setHealth(await getRouteHealth());
    } finally { setLoading(false); }
  };
  useEffect(() => { fetchData(); }, [groupId]);

  const handleCreate = async (values: any) => {
    await createRoute(values);
    message.success('创建成功');
    setModalOpen(false); form.resetFields(); fetchData();
  };

  const getHealthTag = (routeId: number) => {
    const h = health.find((h: RouteHealth) => h.routeId === routeId);
    if (!h) return <Tag>未知</Tag>;
    const color = h.healthStatus === 'HEALTHY' ? 'green' : h.healthStatus === 'DEGRADED' ? 'orange' : 'red';
    return <Tag color={color}>{h.healthStatus === 'HEALTHY' ? '健康' : h.healthStatus === 'DEGRADED' ? '降级' : '不健康'}</Tag>;
  };

  const columns = [
    { title: '线路编码', dataIndex: 'routeCode', key: 'routeCode' },
    { title: '线路类型', dataIndex: 'routeType', key: 'routeType', render: (v: string) => {
      const map: Record<string, string> = { OFFICIAL_REDIRECT: '官方跳转', AUTHORIZED_WEB: '授权H5', SIGNED_VOD: '签名点播', INTERNAL_WEB: '自有页面' };
      return map[v] || v;
    }},
    { title: '目标模板', dataIndex: 'targetTemplate', key: 'targetTemplate', ellipsis: true },
    { title: '优先级', dataIndex: 'priority', key: 'priority', width: 80 },
    { title: '授权', dataIndex: 'authorizationStatus', key: 'authorizationStatus', width: 80, render: (v: string) => (
      <Tag color={v === 'VERIFIED' ? 'green' : 'orange'}>{v === 'VERIFIED' ? '已验证' : '待审核'}</Tag>
    )},
    { title: '启用', dataIndex: 'enabled', key: 'enabled', width: 60, render: (v: boolean) => <Tag color={v ? 'green' : 'red'}>{v ? '是' : '否'}</Tag> },
    { title: '健康状态', key: 'health', width: 100, render: (_: any, r: PlaybackRoute) => getHealthTag(r.id) },
  ];

  return (
    <Card title="线路管理" extra={
      <Space>
        <InputNumber min={1} value={groupId} onChange={(v) => setGroupId(v || 1)} style={{ width: 120 }} addonBefore="组ID" />
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建线路</Button>
      </Space>
    }>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />
      <Modal title="新建线路" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="routeCode" label="线路编码" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="routeType" label="线路类型" rules={[{ required: true }]}>
            <Select options={[
              { label: '官方页面跳转', value: 'OFFICIAL_REDIRECT' },
              { label: '授权H5播放页', value: 'AUTHORIZED_WEB' },
              { label: '签名云点播', value: 'SIGNED_VOD' },
              { label: '自有内容页面', value: 'INTERNAL_WEB' },
            ]} />
          </Form.Item>
          <Form.Item name="targetTemplate" label="目标URL模板"><Input placeholder="https://.../{contentKey}" /></Form.Item>
          <Form.Item name="priority" label="优先级" initialValue={0}><InputNumber min={0} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
