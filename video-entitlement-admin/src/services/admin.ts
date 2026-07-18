import request from '../utils/request';
import type { ApiResponse, PageResult, AdminLoginRequest, AdminLoginResponse, AdminVO, AdminRole, AdminOperationLog, PageParams } from '../types/api';

export const adminLogin = (data: AdminLoginRequest) =>
  request.post<ApiResponse<AdminLoginResponse>>('/admin/api/v1/auth/login', data).then(r => r.data.data);

export const createAdmin = (data: { username: string; password: string; roleCodes: string[] }) =>
  request.post<ApiResponse<AdminVO>>('/admin/api/v1/auth/create-admin', data).then(r => r.data.data);

export const getAdminUsers = (params: PageParams) =>
  request.get<ApiResponse<PageResult<AdminVO>>>('/admin/api/v1/admin/users', { params }).then(r => r.data.data);

export const getRoles = () =>
  request.get<ApiResponse<AdminRole[]>>('/admin/api/v1/admin/roles').then(r => r.data.data);

export const getOperationLogs = (params: PageParams & { userId?: number }) =>
  request.get<ApiResponse<PageResult<AdminOperationLog>>>('/admin/api/v1/admin/operation-logs', { params }).then(r => r.data.data);
