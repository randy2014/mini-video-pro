package com.video.entitlement.module.playback.repository;

import com.video.entitlement.module.playback.entity.PlaybackRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaybackRuleRepository extends JpaRepository<PlaybackRule, Long> {

    List<PlaybackRule> findByPlatformCodeAndEnabledOrderByPriorityAsc(String platformCode, Boolean enabled);
}
