package com.chooz.thumbnail.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {

    Optional<Thumbnail> findByPostId(Long postId);

    List<Thumbnail> findByPostIdIn(Collection<Long> postIds);
}
