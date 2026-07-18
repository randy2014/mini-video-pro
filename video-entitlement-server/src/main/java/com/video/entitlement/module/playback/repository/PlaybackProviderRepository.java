package com.video.entitlement.module.playback.repository;

import com.video.entitlement.module.playback.entity.PlaybackProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaybackProviderRepository extends JpaRepository<PlaybackProvider, Long> {

    Optional<PlaybackProvider> findByProviderCode(String code);
}
