package com.chooz.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        SELECT u.nickname
        FROM User u
        WHERE u.nickname
        LIKE CONCAT(:prefix, '%')
    """)
    List<String> findNicknamesByPrefix(@Param("prefix") String prefix);

}
