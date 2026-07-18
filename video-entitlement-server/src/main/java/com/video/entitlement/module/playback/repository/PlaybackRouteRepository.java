package com.video.entitlement.module.playback.repository;

import com.video.entitlement.module.playback.entity.PlaybackRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaybackRouteRepository extends JpaRepository<PlaybackRoute, Long> {

    Optional<PlaybackRoute> findByRouteCode(String code);

    List<PlaybackRoute> findByGroupIdAndEnabledOrderByPriorityAsc(Long groupId, Boolean enabled);
}
