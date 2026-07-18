import request from '../utils/request';
import type { ApiResponse, PageResult, ConfigRelease, ConfigPublishRequest, PageParams } from '../types/api';

export const getReleases = (params: PageParams) =>
  request.get<ApiResponse<PageResult<ConfigRelease>>>('/admin/api/v1/config/releases', { params }).then(r => r.data.data);

export const createRelease = (data: ConfigPublishRequest) =>
  request.post<ApiResponse<ConfigRelease>>('/admin/api/v1/config/releases', data).then(r => r.data.data);
