package com.video.entitlement.module.user.repository;

import com.video.entitlement.module.user.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUserNo(String userNo);

    Optional<UserAccount> findByMobile(String mobile);

    Page<UserAccount> findByMobileContaining(String mobile, Pageable pageable);
}
