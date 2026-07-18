import request from '../utils/request';
import type { ApiResponse, StatsSummary } from '../types/api';

export const getStatsSummary = () =>
  request.get<ApiResponse<StatsSummary>>('/admin/api/v1/stats/summary').then(r => r.data.data);
