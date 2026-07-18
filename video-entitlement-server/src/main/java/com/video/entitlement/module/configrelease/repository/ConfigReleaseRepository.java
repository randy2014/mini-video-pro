package com.video.entitlement.module.configrelease.repository;

import com.video.entitlement.module.configrelease.entity.ConfigRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigReleaseRepository extends JpaRepository<ConfigRelease, Long> {

    Optional<ConfigRelease> findByReleaseNo(String no);

    Optional<ConfigRelease> findByConfigVersion(String version);
}
