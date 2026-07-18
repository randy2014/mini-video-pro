// API 统一响应
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  requestId: string;
  timestamp: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  page: number;
  size: number;
  totalPages: number;
}

export interface PageParams {
  page: number;
  size: number;
}

// Admin
export interface AdminLoginRequest {
  username: string;
  password: string;
}
export interface AdminLoginResponse {
  adminToken: string;
  username: string;
  permissions: string[];
}
export interface AdminVO {
  id: number;
  username: string;
  status: string;
  lastLoginAt: string;
  createdAt: string;
}

// Entitlement
export interface EntitlementProduct {
  id: number;
  productCode: string;
  productName: string;
  description: string;
  validityType: string;
  validDays: number;
  dailyUsageLimit: number;
  totalUsageLimit: number;
  deviceLimit: number;
  status: string;
  createdAt: string;
}
export interface EntitlementBatch {
  id: number;
  batchNo: string;
  productId: number;
  channelCode: string;
  quantity: number;
  generatedCount: number;
  activatedCount: number;
  status: string;
  createdAt: string;
}
export interface EntitlementBatchRequest {
  productId: number;
  channelCode?: string;
  quantity: number;
}

// Platform
export interface VideoPlatform {
  id: number;
  platformCode: string;
  platformName: string;
  homeUrl: string;
  status: string;
  enabled: boolean;
  createdAt: string;
}
export interface PlatformConfigRequest {
  platformCode: string;
  platformName: string;
  homeUrl: string;
  domains?: string[];
}

// Playback
export interface PlaybackProvider {
  id: number;
  providerCode: string;
  providerName: string;
  providerType: string;
  status: string;
  authorizationStatus: string;
}
export interface PlaybackRoute {
  id: number;
  routeCode: string;
  providerId: number;
  groupId: number;
  routeType: string;
  targetTemplate: string;
  priority: number;
  enabled: boolean;
  authorizationStatus: string;
}
export interface PlaybackRule {
  id: number;
  platformCode: string;
  clientType: string;
  routeGroupId: number;
  priority: number;
  enabled: boolean;
}
export interface PlaybackRouteGroup {
  id: number;
  groupCode: string;
  platformCode: string;
  selectionStrategy: string;
  maximumAttempts: number;
  enabled: boolean;
}
export interface RouteHealth {
  id: number;
  routeId: number;
  healthStatus: string;
  successRate5m: number;
  consecutiveFailureCount: number;
  circuitOpenUntil: string;
}

// Risk
export interface RiskBlacklist {
  id: number;
  blacklistType: string;
  targetValue: string;
  status: string;
  reason: string;
  startTime: string;
}
export interface RiskEvent {
  id: number;
  riskEventType: string;
  userId: number;
  riskLevel: string;
  action: string;
  createdAt: string;
}
export interface RiskRule {
  id: number;
  ruleCode: string;
  eventType: string;
  threshold: number;
  windowSeconds: number;
  action: string;
  enabled: boolean;
}

// ConfigRelease
export interface ConfigRelease {
  id: number;
  releaseNo: string;
  releaseType: string;
  configVersion: string;
  status: string;
  grayPercentage: number;
  description: string;
  publishedAt: string;
  createdAt: string;
}
export interface ConfigPublishRequest {
  releaseType: string;
  description?: string;
  grayPercentage?: number;
}

// Stats
export interface StatsSummary {
  totalUsers: number;
  totalBatches: number;
  totalPlaybackRequests: number;
  totalEntitlements: number;
}

// Operation Log
export interface AdminOperationLog {
  id: number;
  adminUserId: number;
  module: string;
  operation: string;
  businessId: string;
  beforeJson: string;
  afterJson: string;
  result: string;
  requestId: string;
  createdAt: string;
}

// Admin Role
export interface AdminRole {
  id: number;
  roleCode: string;
  roleName: string;
  status: string;
}
