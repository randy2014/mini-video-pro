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

// Platform
export interface VideoPlatform {
  id: number;
  platformCode: string;
  platformName: string;
  platformType: string;
  homeUrl: string;
  logo?: string;
  status: string;
  enabled: boolean;
  createdAt: string;
}
export interface PlatformConfigRequest {
  platformCode: string;
  platformName: string;
  platformType: string;
  homeUrl: string;
  logo?: string;
  domains?: string[];
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

// ====== 权益管理 ======
export interface Entitlement {
  id: number;
  entitlementName: string;
  entitlementCode: string;
  startTime: string;
  endTime: string;
  status: string;
  ownerName: string;
  ownerPhone: string;
  ownerProfession: string;
  createdAt: string;
}

export interface EntitlementRequest {
  entitlementName: string;
  entitlementCode?: string;
  startTime?: string;
  endTime?: string;
  status?: string;
  ownerName?: string;
  ownerPhone?: string;
  ownerProfession?: string;
}

// ====== 用户管理 ======
export interface UserVO {
  id: number;
  userNo: string;
  mobile: string;
  nickname: string;
  status: string;
  riskLevel: string;
  lastLoginAt: string;
  createdAt: string;
  entitlements?: { entitlementCode: string; expireTime: string }[];
}

// ====== APP 版本管理 ======
export interface AppVersion {
  id: number;
  versionName: string;
  versionCode: number;
  downloadUrl: string;
  releaseNotes: string;
  forceUpdate: boolean;
  status: string;
  createdAt: string;
  updatedAt: string;
}
export interface AppVersionRequest {
  versionName?: string;
  versionCode?: number;
  downloadUrl?: string;
  releaseNotes?: string;
  forceUpdate?: boolean;
}
