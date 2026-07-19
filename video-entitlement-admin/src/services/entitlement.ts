import request from '../utils/request';
import type { ApiResponse, PageResult, PageParams, Entitlement, EntitlementRequest } from '../types/api';

export const getEntitlements = (params: PageParams) =>
  request.get<ApiResponse<PageResult<Entitlement>>>('/admin/api/v1/entitlements', { params }).then(r => r.data.data);

export const getEntitlement = (id: number) =>
  request.get<ApiResponse<Entitlement>>(`/admin/api/v1/entitlements/${id}`).then(r => r.data.data);

export const createEntitlement = (data: EntitlementRequest) =>
  request.post<ApiResponse<Entitlement>>('/admin/api/v1/entitlements', data).then(r => r.data.data);

export const updateEntitlement = (id: number, data: EntitlementRequest) =>
  request.put<ApiResponse<Entitlement>>(`/admin/api/v1/entitlements/${id}`, data).then(r => r.data.data);

export const deleteEntitlement = (id: number) =>
  request.delete<ApiResponse<null>>(`/admin/api/v1/entitlements/${id}`).then(r => r.data);
