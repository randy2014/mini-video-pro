package com.video.entitlement.module.user.repository;

import com.video.entitlement.module.user.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUserNo(String userNo);

    Optional<UserAccount> findByMobile(String mobile);
}
