package com.chooz.post.domain;

import com.chooz.post.presentation.dto.PollChoiceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollChoiceRepository extends JpaRepository<PollChoice, Long> {

}
