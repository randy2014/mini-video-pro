import request from '../utils/request';
import type { ApiResponse, VideoPlatform, PlatformConfigRequest } from '../types/api';

export const getPlatforms = () =>
  request.get<ApiResponse<VideoPlatform[]>>('/admin/api/v1/platform').then(r => r.data.data);

export const createPlatform = (data: PlatformConfigRequest) =>
  request.post<ApiResponse<VideoPlatform>>('/admin/api/v1/platform', data).then(r => r.data.data);

export const addDomain = (platformId: number, host: string) =>
  request.post<ApiResponse<void>>(`/admin/api/v1/platform/${platformId}/domains`, null, { params: { host } }).then(r => r.data.data);

export const addRule = (platformId: number, data: { ruleType: string; pattern: string; priority: number }) =>
  request.post<ApiResponse<void>>(`/admin/api/v1/platform/${platformId}/rules`, null, { params: data }).then(r => r.data.data);

export const updatePlatform = (id: number, data: PlatformConfigRequest) =>
  request.put<ApiResponse<VideoPlatform>>(`/admin/api/v1/platform/${id}`, data).then(r => r.data.data);

export const deletePlatform = (id: number) =>
  request.delete<ApiResponse<void>>(`/admin/api/v1/platform/${id}`).then(r => r.data.data);
