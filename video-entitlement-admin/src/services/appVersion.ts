import request from '../utils/request';
import type { ApiResponse, AppVersion, AppVersionRequest } from '../types/api';

export const getVersions = () =>
  request.get<ApiResponse<AppVersion[]>>('/admin/api/v1/versions').then(r => r.data.data);

export const createVersion = (data: AppVersionRequest) =>
  request.post<ApiResponse<AppVersion>>('/admin/api/v1/versions', data).then(r => r.data.data);

// 文件上传（multipart），data 为 FormData
export const uploadVersion = (data: FormData) =>
  request.post<ApiResponse<AppVersion>>('/admin/api/v1/versions/upload', data).then(r => r.data.data);

export const updateVersion = (id: number, data: AppVersionRequest) =>
  request.put<ApiResponse<AppVersion>>(`/admin/api/v1/versions/${id}`, data).then(r => r.data.data);

// 替换已有版本的 APK 文件（multipart）
export const replaceApk = (id: number, data: FormData) =>
  request.post<ApiResponse<AppVersion>>(`/admin/api/v1/versions/${id}/upload`, data).then(r => r.data.data);

export const deleteVersion = (id: number) =>
  request.delete<ApiResponse<void>>(`/admin/api/v1/versions/${id}`).then(r => r.data.data);

export const toggleVersionStatus = (id: number, status: string) =>
  request.put<ApiResponse<AppVersion>>(`/admin/api/v1/versions/${id}/status`, null, { params: { status } }).then(r => r.data.data);
