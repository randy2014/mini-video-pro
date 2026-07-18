package com.video.entitlement.module.platform.repository;

import com.video.entitlement.module.platform.entity.VideoUrlRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoUrlRuleRepository extends JpaRepository<VideoUrlRule, Long> {

    List<VideoUrlRule> findByPlatformIdAndEnabledOrderByPriorityAsc(Long platformId, Boolean enabled);

    @Modifying
    void deleteByPlatformId(Long platformId);
}
