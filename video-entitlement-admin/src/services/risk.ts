import request from '../utils/request';
import type { ApiResponse, PageResult, RiskBlacklist, RiskEvent, RiskRule, PageParams } from '../types/api';

export const getBlacklist = () =>
  request.get<ApiResponse<RiskBlacklist[]>>('/admin/api/v1/risk/blacklist').then(r => r.data.data);

export const addBlacklist = (data: { type: string; value: string; reason: string }) =>
  request.post<ApiResponse<RiskBlacklist>>('/admin/api/v1/risk/blacklist', null, { params: data }).then(r => r.data.data);

export const getRiskEvents = (params: PageParams) =>
  request.get<ApiResponse<PageResult<RiskEvent>>>('/admin/api/v1/risk/events', { params }).then(r => r.data.data);

export const getRiskRules = () =>
  request.get<ApiResponse<RiskRule[]>>('/admin/api/v1/risk/rules').then(r => r.data.data);
