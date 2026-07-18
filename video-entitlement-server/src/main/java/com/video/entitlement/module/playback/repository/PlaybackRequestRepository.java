package com.video.entitlement.module.playback.repository;

import com.video.entitlement.module.playback.entity.PlaybackRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaybackRequestRepository extends JpaRepository<PlaybackRequest, Long> {

    Optional<PlaybackRequest> findByRequestNo(String requestNo);
}
