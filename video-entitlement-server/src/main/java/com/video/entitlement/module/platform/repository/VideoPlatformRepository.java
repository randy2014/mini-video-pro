package com.video.entitlement.module.platform.repository;

import com.video.entitlement.module.platform.entity.VideoPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoPlatformRepository extends JpaRepository<VideoPlatform, Long> {

    Optional<VideoPlatform> findByPlatformCode(String code);

    List<VideoPlatform> findAllByStatus(String status);
}
