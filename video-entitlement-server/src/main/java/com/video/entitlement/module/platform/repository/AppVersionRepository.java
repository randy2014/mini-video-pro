package com.video.entitlement.module.platform.repository;

import com.video.entitlement.module.platform.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AppVersionRepository extends JpaRepository<AppVersion, Long> {

    /**
     * 查询当前激活的最新版本（按 versionCode 降序）
     */
    Optional<AppVersion> findTopByStatusOrderByVersionCodeDesc(String status);

    /**
     * 按 versionCode 查找
     */
    Optional<AppVersion> findByVersionCode(Integer versionCode);
}
