import request from '../utils/request';
import type { ApiResponse, PlaybackProvider, PlaybackRoute, PlaybackRule, PlaybackRouteGroup, RouteHealth } from '../types/api';

export const getProviders = () =>
  request.get<ApiResponse<PlaybackProvider[]>>('/admin/api/v1/playback/providers').then(r => r.data.data);

export const createProvider = (data: Partial<PlaybackProvider>) =>
  request.post<ApiResponse<PlaybackProvider>>('/admin/api/v1/playback/providers', data).then(r => r.data.data);

export const getRouteGroups = () =>
  request.get<ApiResponse<PlaybackRouteGroup[]>>('/admin/api/v1/playback/groups').then(r => r.data.data);

export const getRoutes = (groupId: number) =>
  request.get<ApiResponse<PlaybackRoute[]>>('/admin/api/v1/playback/routes', { params: { groupId } }).then(r => r.data.data);

export const createRoute = (data: Partial<PlaybackRoute>) =>
  request.post<ApiResponse<PlaybackRoute>>('/admin/api/v1/playback/routes', data).then(r => r.data.data);

export const getRules = (platformCode: string) =>
  request.get<ApiResponse<PlaybackRule[]>>('/admin/api/v1/playback/rules', { params: { platformCode } }).then(r => r.data.data);

export const createRule = (data: Partial<PlaybackRule>) =>
  request.post<ApiResponse<PlaybackRule>>('/admin/api/v1/playback/rules', data).then(r => r.data.data);

export const getRouteHealth = () =>
  request.get<ApiResponse<RouteHealth[]>>('/admin/api/v1/playback/health').then(r => r.data.data);
