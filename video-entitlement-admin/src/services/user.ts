import request from '../utils/request';
import type { ApiResponse, PageResult, PageParams, UserVO } from '../types/api';

export const getUsers = (params: PageParams & { keyword?: string }) =>
  request.get<ApiResponse<PageResult<UserVO>>>('/admin/api/v1/users', { params }).then(r => r.data.data);

export const getUser = (id: number) =>
  request.get<ApiResponse<UserVO>>(`/admin/api/v1/users/${id}`).then(r => r.data.data);

export const updateUserStatus = (id: number, status: string) =>
  request.put<ApiResponse<null>>(`/admin/api/v1/users/${id}/status`, null, { params: { status } }).then(r => r.data);
