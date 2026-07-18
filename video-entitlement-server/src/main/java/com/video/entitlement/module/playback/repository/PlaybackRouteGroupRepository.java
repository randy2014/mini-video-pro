package com.video.entitlement.module.playback.repository;

import com.video.entitlement.module.playback.entity.PlaybackRouteGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaybackRouteGroupRepository extends JpaRepository<PlaybackRouteGroup, Long> {

    Optional<PlaybackRouteGroup> findByGroupCode(String code);
}
