import { useEffect, useState } from 'react';
import {
  Card, Table, Button, Modal, Form, Input, InputNumber, Switch, Tag, Upload, Popconfirm, Space, App,
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, UploadOutlined,
} from '@ant-design/icons';
import type { UploadProps } from 'antd';
import { getVersions, createVersion, uploadVersion, updateVersion, replaceApk, deleteVersion, toggleVersionStatus } from '../../services/appVersion';
import type { AppVersion, AppVersionRequest } from '../../types/api';
import dayjs from 'dayjs';

const fmt = (s?: string) => (s ? dayjs(s).format('YYYY-MM-DD HH:mm') : '-');

export default function AppVersionManage() {
  const [data, setData] = useState<AppVersion[]>([]);
  const [loading, setLoading] = useState(false);
  const [createOpen, setCreateOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [editing, setEditing] = useState<AppVersion | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [createForm] = Form.useForm();
  const [editForm] = Form.useForm();
  const { message } = App.useApp();

  const fetchData = () => {
    setLoading(true);
    getVersions()
      .then(setData)
      .catch(() => message.error('加载失败'))
      .finally(() => setLoading(false));
  };

  useEffect(fetchData, []);

  const resetFile = () => setFile(null);

  const openCreate = () => {
    createForm.resetFields();
    resetFile();
    setCreateOpen(true);
  };

  const openEdit = (record: AppVersion) => {
    setEditing(record);
    editForm.setFieldsValue({
      versionName: record.versionName,
      versionCode: record.versionCode,
      releaseNotes: record.releaseNotes,
      forceUpdate: record.forceUpdate,
    });
    resetFile();
    setEditOpen(true);
  };

  const handleCreate = async (values: any) => {
    setSubmitting(true);
    try {
      if (file) {
        const fd = new FormData();
        fd.append('file', file);
        fd.append('versionName', values.versionName);
        fd.append('versionCode', String(values.versionCode));
        fd.append('releaseNotes', values.releaseNotes || '');
        fd.append('forceUpdate', String(!!values.forceUpdate));
        await uploadVersion(fd);
      } else {
        if (!values.downloadUrl) {
          message.warning('请上传 APK 文件或填写下载地址');
          setSubmitting(false);
          return;
        }
        const payload: AppVersionRequest = {
          versionName: values.versionName,
          versionCode: values.versionCode,
          releaseNotes: values.releaseNotes,
          forceUpdate: !!values.forceUpdate,
          downloadUrl: values.downloadUrl,
        };
        await createVersion(payload);
      }
      message.success('发布成功');
      setCreateOpen(false);
      fetchData();
    } catch {
      // 错误提示由拦截器统一处理
    } finally {
      setSubmitting(false);
    }
  };

  const handleEdit = async (values: any) => {
    if (!editing) return;
    setSubmitting(true);
    try {
      if (file) {
        const fd = new FormData();
        fd.append('file', file);
        await replaceApk(editing.id, fd);
      }
      const payload: AppVersionRequest = {
        versionName: values.versionName,
        versionCode: values.versionCode,
        releaseNotes: values.releaseNotes,
        forceUpdate: !!values.forceUpdate,
      };
      await updateVersion(editing.id, payload);
      message.success('更新成功');
      setEditOpen(false);
      fetchData();
    } catch {
      // 错误提示由拦截器统一处理
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteVersion(id);
      message.success('已删除');
      fetchData();
    } catch {
      // 错误提示由拦截器统一处理
    }
  };

  const handleToggle = async (record: AppVersion) => {
    const next = record.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    try {
      await toggleVersionStatus(record.id, next);
      message.success(next === 'ACTIVE' ? '已启用' : '已停用');
      fetchData();
    } catch {
      // 错误提示由拦截器统一处理
    }
  };

  const beforeUpload: UploadProps['beforeUpload'] = (f) => {
    setFile(f as File);
    return false; // 阻止自动上传，仅暂存文件
  };

  const columns = [
    { title: '版本名', dataIndex: 'versionName', key: 'versionName', width: 140 },
    { title: '版本号', dataIndex: 'versionCode', key: 'versionCode', width: 100, render: (v: number) => `v${v}` },
    {
      title: '下载地址', dataIndex: 'downloadUrl', key: 'downloadUrl', ellipsis: true,
      render: (url: string) => url
        ? <a href={url} target="_blank" rel="noreferrer">{url}</a>
        : <span style={{ color: '#999' }}>未设置</span>,
    },
    { title: '更新说明', dataIndex: 'releaseNotes', key: 'releaseNotes', ellipsis: true, render: (v?: string) => v || '-' },
    {
      title: '强制更新', dataIndex: 'forceUpdate', key: 'forceUpdate', width: 100,
      render: (v: boolean) => <Tag color={v ? 'red' : 'default'}>{v ? '强制' : '非强制'}</Tag>,
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 100,
      render: (v: string) => <Tag color={v === 'ACTIVE' ? 'green' : 'default'}>{v}</Tag>,
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (s: string) => fmt(s) },
    { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt', width: 160, render: (s: string) => fmt(s) },
    {
      title: '操作', key: 'action', width: 200, fixed: 'right' as const,
      render: (_: unknown, record: AppVersion) => (
        <Space size="small">
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除该版本？" description="将同时删除服务器上的 APK 文件" onConfirm={() => handleDelete(record.id)} okText="删除" cancelText="取消">
            <Button size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
          <Button size="small" onClick={() => handleToggle(record)}>{record.status === 'ACTIVE' ? '停用' : '启用'}</Button>
        </Space>
      ),
    },
  ];

  return (
    <Card
      title="APP 版本发布管理"
      extra={<Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>发布新版本</Button>}
    >
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data}
        loading={loading}
        pagination={false}
        scroll={{ x: 1100 }}
      />

      {/* 发布新版本 */}
      <Modal
        title="发布新版本"
        open={createOpen}
        onCancel={() => setCreateOpen(false)}
        onOk={() => createForm.submit()}
        confirmLoading={submitting}
        destroyOnClose
      >
        <Form form={createForm} layout="vertical" onFinish={handleCreate}>
          <Form.Item name="versionName" label="版本名（如 v1.3）" rules={[{ required: true, message: '请输入版本名' }]}>
            <Input placeholder="例如 v1.3" />
          </Form.Item>
          <Form.Item name="versionCode" label="版本号（正整数，须唯一）" rules={[{ required: true, message: '请输入版本号' }]}>
            <InputNumber min={1} style={{ width: '100%' }} placeholder="例如 3" />
          </Form.Item>
          <Form.Item name="releaseNotes" label="更新说明">
            <Input.TextArea rows={3} placeholder="将展示在 App 更新弹窗中" />
          </Form.Item>
          <Form.Item name="forceUpdate" label="强制更新" valuePropName="checked" initialValue={false}>
            <Switch />
          </Form.Item>
          <Form.Item label="APK 文件上传（与下载地址二选一）">
            <Space direction="vertical" style={{ width: '100%' }}>
              <Upload beforeUpload={beforeUpload} maxCount={1} showUploadList={false}>
                <Button icon={<UploadOutlined />}>选择 APK 文件</Button>
              </Upload>
              {file ? <span style={{ color: '#1677ff' }}>已选：{file.name}</span>
                : <span style={{ color: '#999' }}>未选择文件（可改为下方填写下载地址）</span>}
            </Space>
          </Form.Item>
          <Form.Item name="downloadUrl" label="下载地址（不传文件时必填）" extra="例如 http://43.161.222.78:8082/downloads/xxx.apk">
            <Input placeholder="手动填写外部下载地址" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑版本 */}
      <Modal
        title="编辑版本"
        open={editOpen}
        onCancel={() => setEditOpen(false)}
        onOk={() => editForm.submit()}
        confirmLoading={submitting}
        destroyOnClose
      >
        <Form form={editForm} layout="vertical" onFinish={handleEdit}>
          <Form.Item name="versionName" label="版本名" rules={[{ required: true, message: '请输入版本名' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="versionCode" label="版本号（正整数，须唯一）" rules={[{ required: true, message: '请输入版本号' }]}>
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="releaseNotes" label="更新说明">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item name="forceUpdate" label="强制更新" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item label="重新上传 APK（可选，留空则保留原文件）">
            <Space direction="vertical" style={{ width: '100%' }}>
              <Upload beforeUpload={beforeUpload} maxCount={1} showUploadList={false}>
                <Button icon={<UploadOutlined />}>重新选择 APK 文件</Button>
              </Upload>
              {file ? <span style={{ color: '#1677ff' }}>已选：{file.name}</span>
                : <span style={{ color: '#999' }}>当前：{editing?.downloadUrl || '未设置'}</span>}
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
