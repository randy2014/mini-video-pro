package com.video.entitlement.module.configrelease.repository;

import com.video.entitlement.module.configrelease.entity.ConfigReleaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigReleaseItemRepository extends JpaRepository<ConfigReleaseItem, Long> {

    List<ConfigReleaseItem> findByReleaseId(Long releaseId);
}
