package com.chooz.vote.presentation;

import com.chooz.auth.domain.UserInfo;
import com.chooz.vote.presentation.dto.VoteStatusResponse;
import com.chooz.vote.application.VoteService;
import com.chooz.vote.presentation.dto.VoteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/votes")
    public ResponseEntity<Void> vote(
            @Valid @RequestBody VoteRequest request,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        voteService.vote(userInfo.userId(), request.postId(), request.pollChoiceIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/votes/status")
    public ResponseEntity<List<VoteStatusResponse>> findVoteStatus(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal UserInfo userInfo
    ) {
        return ResponseEntity.ok(voteService.findVoteStatus(userInfo.userId(), postId));
    }
}
