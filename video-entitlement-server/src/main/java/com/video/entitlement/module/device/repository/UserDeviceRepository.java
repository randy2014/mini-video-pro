package com.video.entitlement.module.device.repository;

import com.video.entitlement.module.device.entity.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByUserIdAndDevicePublicId(Long userId, String devicePublicId);

    List<UserDevice> findByUserId(Long userId);

    long countByUserIdAndStatus(Long userId, String status);
}
