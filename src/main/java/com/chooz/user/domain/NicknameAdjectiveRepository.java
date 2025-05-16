package com.chooz.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NicknameAdjectiveRepository extends JpaRepository<NicknameAdjective, Long> {

    @Query("""
            SELECT na
            FROM NicknameAdjective na
            ORDER BY RAND()
            LIMIT 1
            """
    )
    Optional<NicknameAdjective> findRandomNicknameAdjective();
}
