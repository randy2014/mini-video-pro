package com.video.entitlement.module.playback.repository;

import com.video.entitlement.module.playback.entity.PlaybackResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaybackResultRepository extends JpaRepository<PlaybackResult, Long> {

    Optional<PlaybackResult> findByPlaybackRequestId(Long requestId);
}
