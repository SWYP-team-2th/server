package com.chooz.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
    SELECT u
    FROM User u
    JOIN FETCH u.onboardingSteps
    where u.id = :userId
    """)
    Optional<User> findByIdFetchOnboardingSteps(@Param("userId") Long userId);
}
