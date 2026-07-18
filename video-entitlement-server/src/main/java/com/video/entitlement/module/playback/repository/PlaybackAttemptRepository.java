package com.video.entitlement.module.playback.repository;

import com.video.entitlement.module.playback.entity.PlaybackAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaybackAttemptRepository extends JpaRepository<PlaybackAttempt, Long> {

    List<PlaybackAttempt> findByPlaybackRequestId(Long requestId);

    Optional<PlaybackAttempt> findByPlaybackRequestIdAndAttemptNo(Long requestId, int attemptNo);
}
