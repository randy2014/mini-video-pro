package com.video.entitlement.module.user.repository;

import com.video.entitlement.module.user.entity.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
    Optional<InviteCode> findByUserId(Long userId);
    Optional<InviteCode> findByCode(String code);
}
