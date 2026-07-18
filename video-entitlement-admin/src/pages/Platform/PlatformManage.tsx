import { useEffect, useState } from 'react';
import { Card, Table, Button, Modal, Form, Input, Select, InputNumber, Space, Tag, App } from 'antd';
import { PlusOutlined, LinkOutlined } from '@ant-design/icons';
import { getPlatforms, createPlatform, addDomain, addRule } from '../../services/platform';
import type { VideoPlatform } from '../../types/api';

export default function PlatformManage() {
  const [data, setData] = useState<VideoPlatform[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [domainModal, setDomainModal] = useState<{ open: boolean; platformId: number }>({ open: false, platformId: 0 });
  const [ruleModal, setRuleModal] = useState<{ open: boolean; platformId: number }>({ open: false, platformId: 0 });
  const [form] = Form.useForm();
  const [domainForm] = Form.useForm();
  const [ruleForm] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = async () => {
    setLoading(true);
    try { setData(await getPlatforms()); } finally { setLoading(false); }
  };
  useEffect(() => { fetchData(); }, []);

  const handleCreate = async (values: any) => {
    await createPlatform({ ...values, domains: values.domains ? [values.domains] : [] });
    message.success('平台创建成功');
    setModalOpen(false); form.resetFields(); fetchData();
  };

  const handleAddDomain = async (values: { host: string }) => {
    await addDomain(domainModal.platformId, values.host);
    message.success('域名添加成功');
    setDomainModal({ open: false, platformId: 0 }); domainForm.resetFields();
  };

  const handleAddRule = async (values: { ruleType: string; pattern: string; priority: number }) => {
    await addRule(ruleModal.platformId, values);
    message.success('规则添加成功');
    setRuleModal({ open: false, platformId: 0 }); ruleForm.resetFields();
  };

  const columns = [
    { title: '平台编码', dataIndex: 'platformCode', key: 'platformCode' },
    { title: '平台名称', dataIndex: 'platformName', key: 'platformName' },
    { title: '首页URL', dataIndex: 'homeUrl', key: 'homeUrl', ellipsis: true },
    { title: '状态', dataIndex: 'status', key: 'status', render: (v: string) => (
      <Tag color={v === 'ACTIVE' ? 'green' : 'orange'}>{v === 'ACTIVE' ? '正常' : v}</Tag>
    )},
    { title: '操作', key: 'actions', render: (_: any, record: VideoPlatform) => (
      <Space>
        <Button size="small" icon={<LinkOutlined />} onClick={() => setDomainModal({ open: true, platformId: record.id })}>域名</Button>
        <Button size="small" onClick={() => setRuleModal({ open: true, platformId: record.id })}>规则</Button>
      </Space>
    )},
  ];

  return (
    <Card title="平台管理" extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>新建平台</Button>}>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading} pagination={false} />

      <Modal title="新建平台" open={modalOpen} onCancel={() => setModalOpen(false)} onOk={() => form.submit()}>
        <Form form={form} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="platformCode" label="平台编码" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="platformName" label="平台名称" rules={[{ required: true }]}><Input /></Form.Item>
          <Form.Item name="homeUrl" label="首页URL" rules={[{ required: true }, { type: 'url' }]}><Input /></Form.Item>
          <Form.Item name="domains" label="域名"><Input placeholder="example.com" /></Form.Item>
        </Form>
      </Modal>

      <Modal title="添加域名" open={domainModal.open} onCancel={() => setDomainModal({ open: false, platformId: 0 })} onOk={() => domainForm.submit()}>
        <Form form={domainForm} layout="vertical" onFinish={handleAddDomain}>
          <Form.Item name="host" label="域名" rules={[{ required: true }]}><Input placeholder="v.qq.com" /></Form.Item>
        </Form>
      </Modal>

      <Modal title="添加URL规则" open={ruleModal.open} onCancel={() => setRuleModal({ open: false, platformId: 0 })} onOk={() => ruleForm.submit()}>
        <Form form={ruleForm} layout="vertical" onFinish={handleAddRule}>
          <Form.Item name="ruleType" label="规则类型" rules={[{ required: true }]}>
            <Select options={[{ label: '正则表达式', value: 'REGEX' }, { label: '前缀匹配', value: 'PREFIX' }, { label: '域名路径', value: 'HOST_PATH' }]} />
          </Form.Item>
          <Form.Item name="pattern" label="匹配模式" rules={[{ required: true }]}><Input placeholder="https://v.qq.com/x/.*" /></Form.Item>
          <Form.Item name="priority" label="优先级"><InputNumber min={0} defaultValue={0} /></Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
