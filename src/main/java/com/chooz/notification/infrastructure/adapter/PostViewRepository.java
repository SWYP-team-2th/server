package com.chooz.notification.infrastructure.adapter;

import com.chooz.notification.application.port.view.PostView;
import com.chooz.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

interface PostViewRepository extends Repository<User, Long> {
    @Query("""
            select new com.chooz.notification.application.port.view.PostView(p.id, p.imageUrl)
            from Comment c
            join Post p on p.id = c.postId
            where c.id = :commentId
    """)
    Optional<PostView> findViewById(Long commentId);
}
