import { useEffect, useState } from 'react';
import { Row, Col, Card, Statistic, Table, Tag, Space } from 'antd';
import { UserOutlined, GiftOutlined, PlayCircleOutlined, TrophyOutlined } from '@ant-design/icons';
import { getStatsSummary } from '../../services/stats';
import { getOperationLogs } from '../../services/admin';
import type { StatsSummary, AdminOperationLog } from '../../types/api';
import dayjs from 'dayjs';

const fmt = (s?: string) => (s ? dayjs(s).format('YYYY-MM-DD HH:mm') : '-');

export default function Dashboard() {
  const [stats, setStats] = useState<StatsSummary | null>(null);
  const [logs, setLogs] = useState<AdminOperationLog[]>([]);

  useEffect(() => {
    getStatsSummary().then(setStats).catch(() => {});
    getOperationLogs({ page: 1, size: 10 }).then((r) => setLogs(r.records)).catch(() => {});
  }, []);

  const logColumns = [
    { title: '模块', dataIndex: 'module', key: 'module', width: 100 },
    { title: '操作', dataIndex: 'operation', key: 'operation', width: 100 },
    { title: '结果', dataIndex: 'result', key: 'result', width: 80, render: (v: string) => <Tag color={v === 'SUCCESS' ? 'green' : 'red'}>{v}</Tag> },
    { title: '时间', dataIndex: 'createdAt', key: 'createdAt', width: 180, render: (s: string) => fmt(s) },
  ];

  return (
    <div>
      <Row gutter={[16, 16]} className="dashboard-stats">
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="用户总数" value={stats?.totalUsers || 0} prefix={<UserOutlined />} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="权益批次" value={stats?.totalBatches || 0} prefix={<GiftOutlined />} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="播放请求" value={stats?.totalPlaybackRequests || 0} prefix={<PlayCircleOutlined />} /></Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card><Statistic title="权益总数" value={stats?.totalEntitlements || 0} prefix={<TrophyOutlined />} /></Card>
        </Col>
      </Row>
      <Card title="最近操作日志" style={{ marginTop: 24 }}>
        <Table columns={logColumns} dataSource={logs} rowKey="id" pagination={false} size="small" />
      </Card>
    </div>
  );
}
