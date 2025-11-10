package com.chooz.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

    Optional<SocialAccount> findBySocialIdAndProvider(String socialId, Provider provider);

    void deleteByUserId(Long userId);

    Optional<SocialAccount> findByUserId(Long userId);
}
