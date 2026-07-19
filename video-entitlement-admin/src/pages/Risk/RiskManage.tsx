import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, Tabs, Tag, App, Space } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getBlacklist, addBlacklist, getRiskEvents, getRiskRules } from '../../services/risk';
import type { RiskBlacklist, RiskEvent, RiskRule } from '../../types/api';
import dayjs from 'dayjs';

export default function RiskManage() {
  const [blacklist, setBlacklist] = useState<RiskBlacklist[]>([]);
  const [events, setEvents] = useState<RiskEvent[]>([]);
  const [rules, setRules] = useState<RiskRule[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [form] = Form.useForm();
  const { message } = App.useApp();
  const fmt = (s?: string) => s ? dayjs(s).format('YYYY-MM-DD HH:mm') : '-';

  useEffect(() => {
    getBlacklist().then(setBlacklist).catch(() => {});
    getRiskEvents({ page: 1, size: 50 }).then(r => setEvents(r.records)).catch(() => {});
    getRiskRules().then(setRules).catch(() => {});
  }, []);

  const handleAdd = async (values: any) => {
    await addBlacklist(values);
    message.success('添加成功');
    setModalOpen(false); form.resetFields();
    getBlacklist().then(setBlacklist);
  };

  const blColumns = [
    { title: '类型', dataIndex: 'blacklistType', key: 'blacklistType', render: (v: string) => (
      <Tag>{v === 'USER' ? '用户' : v === 'DEVICE' ? '设备' : 'IP'}</Tag>
    )},
    { title: '目标值', dataIndex: 'targetValue', key: 'targetValue' },
    { title: '原因', dataIndex: 'reason', key: 'reason' },
    { title: '状态', dataIndex: 'status', key: 'status', render: (v: string) => <Tag color={v === 'ACTIVE' ? 'red' : 'default'}>{v}</Tag> },
  ];
  const evtColumns = [
    { title: '事件类型', dataIndex: 'riskEventType', key: 'riskEventType' },
    { title: '用户ID', dataIndex: 'userId', key: 'userId' },
    { title: '风险等级', dataIndex: 'riskLevel', key: 'riskLevel', render: (v: string) => (
      <Tag color={v === 'CRITICAL' ? 'red' : v === 'HIGH' ? 'orange' : 'blue'}>{v}</Tag>
    )},
    { title: '动作', dataIndex: 'action', key: 'action', render: (v: string) => (
      <Tag color={v === 'BLOCK' ? 'red' : v === 'BAN' ? 'volcano' : 'blue'}>{v}</Tag>
    )},
    { title: '时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (s: string) => fmt(s) },
  ];
  const ruleColumns = [
    { title: '规则编码', dataIndex: 'ruleCode', key: 'ruleCode' },
    { title: '事件类型', dataIndex: 'eventType', key: 'eventType' },
    { title: '阈值', dataIndex: 'threshold', key: 'threshold' },
    { title: '窗口(秒)', dataIndex: 'windowSeconds', key: 'windowSeconds' },
    { title: '动作', dataIndex: 'action', key: 'action', render: (v: string) => <Tag color="volcano">{v}</Tag> },
    { title: '启用', dataIndex: 'enabled', key: 'enabled', render: (v: boolean) => <Tag color={v ? 'green' : 'red'}>{v ? '是' : '否'}</Tag> },
  ];

  return (
    <Card title="风控管理" extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>添加黑名单</Button>}>
      <Tabs items={[
        { key: 'blacklist', label: '黑名单', children: <Table columns={blColumns} dataSource={blacklist} rowKey="id" pagination={false} size="small" /> },
        { key: 'events', label: '风险事件', children: <Table columns={evtColumns} dataSource={events} rowKey="id" pagination={false} size="small" /> },
        { key: 'rules', label: '风控规则', children: <Table columns={ruleColumns} dataSource={rules} rowKey="id" pagination={false} size="small" /> },
      ]} />
      <Modal title="添加黑名单" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleAdd}>
          <Form.Item name="type" label="类型" rules={[{ required: true }]}>
            <Select options={[{ label: '用户', value: 'USER' }, { label: '设备', value: 'DEVICE' }, { label: 'IP', value: 'IP' }]} />
          </Form.Item>
          <Form.Item name="value" label="目标值" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="reason" label="原因"><Input.TextArea rows={2} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
