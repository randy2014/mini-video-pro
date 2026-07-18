package com.video.entitlement.module.platform.repository;

import com.video.entitlement.module.platform.entity.VideoPlatformDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoPlatformDomainRepository extends JpaRepository<VideoPlatformDomain, Long> {

    List<VideoPlatformDomain> findByPlatformId(Long platformId);

    Optional<VideoPlatformDomain> findByPlatformIdAndHost(Long platformId, String host);
}
