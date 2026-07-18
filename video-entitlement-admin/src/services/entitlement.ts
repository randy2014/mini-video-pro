import request from '../utils/request';
import type { ApiResponse, PageResult, EntitlementProduct, EntitlementBatch, EntitlementBatchRequest, PageParams } from '../types/api';

export const getProducts = (params: PageParams) =>
  request.get<ApiResponse<PageResult<EntitlementProduct>>>('/admin/api/v1/entitlement/products', { params }).then(r => r.data.data);

export const createProduct = (data: Partial<EntitlementProduct>) =>
  request.post<ApiResponse<EntitlementProduct>>('/admin/api/v1/entitlement/products', data).then(r => r.data.data);

export const getBatches = (params: PageParams) =>
  request.get<ApiResponse<PageResult<EntitlementBatch>>>('/admin/api/v1/entitlement/batches', { params }).then(r => r.data.data);

export const createBatch = (data: EntitlementBatchRequest) =>
  request.post<ApiResponse<EntitlementBatch>>('/admin/api/v1/entitlement/batches', data).then(r => r.data.data);
