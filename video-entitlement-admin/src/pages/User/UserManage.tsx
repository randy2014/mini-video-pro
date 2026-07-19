import { useEffect, useState } from 'react';
import { Table, Button, Input, Space, Popconfirm, message, Tag } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { getUsers, updateUserStatus } from '../../services/user';
import type { UserVO } from '../../types/api';
import dayjs from 'dayjs';

export default function UserManage() {
  const [data, setData] = useState<UserVO[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState('');

  const fmt = (s?: string) => s ? dayjs(s).format('YYYY-MM-DD HH:mm') : '-';

  const fetch = (p = 0) => {
    setLoading(true);
    getUsers({ page: p, size: 10, keyword: keyword || undefined })
      .then((res) => { setData(res.records); setTotal(res.total); setPage(res.page); })
      .catch(() => message.error('加载失败'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetch(); }, []);

  const toggleStatus = async (record: UserVO) => {
    const newStatus = record.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE';
    await updateUserStatus(record.id, newStatus);
    message.success('状态已更新');
    fetch(page);
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', width: 60 },
    { title: '手机号', dataIndex: 'mobile', width: 120 },
    { title: '昵称', dataIndex: 'nickname', width: 100 },
    {
      title: '权益码 / 到期时间', width: 220,
      render: (_: unknown, record: UserVO) => {
        const list = record.entitlements || [];
        if (list.length === 0) return <span style={{ color: '#999' }}>暂无权益</span>;
        return (
          <Space direction="vertical" size={2}>
            {list.map((e, i) => (
              <span key={i}>
                <Tag color="blue">{e.entitlementCode}</Tag>
                {e.expireTime ? <Tag color={new Date(e.expireTime) > new Date() ? 'green' : 'red'}>{e.expireTime}</Tag> : <Tag>永久</Tag>}
              </span>
            ))}
          </Space>
        );
      },
    },
    { title: '风险等级', dataIndex: 'riskLevel', width: 80 },
    {
      title: '状态', dataIndex: 'status', width: 80,
      render: (s: string) => s === 'ACTIVE' ? '✅ 正常' : '⛔ 禁用',
    },
    { title: '最后登录', dataIndex: 'lastLoginAt', width: 140, render: (s: string) => fmt(s) },
    { title: '注册时间', dataIndex: 'createdAt', width: 140, render: (s: string) => fmt(s) },
    {
      title: '操作', width: 100,
      render: (_: unknown, record: UserVO) => (
        <Popconfirm title={`确认${record.status === 'ACTIVE' ? '禁用' : '启用'}？`} onConfirm={() => toggleStatus(record)}>
          <Button size="small" danger={record.status === 'ACTIVE'}>{record.status === 'ACTIVE' ? '禁用' : '启用'}</Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Input placeholder="搜索手机号" value={keyword} onChange={(e) => setKeyword(e.target.value)}
          onPressEnter={() => fetch()} style={{ width: 200 }} />
        <Button icon={<SearchOutlined />} onClick={() => fetch()}>搜索</Button>
      </Space>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
        pagination={{ current: page + 1, total, pageSize: 10, onChange: (p) => fetch(p - 1) }} size="small" />
    </div>
  );
}
