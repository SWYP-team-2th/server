package com.chooz.notification.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.notification.application.dto.CommentLikedContent;
import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.application.dto.VotedContent;
import com.chooz.notification.domain.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationContentAssembler {

    private final NotificationQueryRepository notificationQueryDslRepository;

    public CommentLikedContent forCommentLiked(Long commentId, Long likerId) {
        TargetUserDto targetUserDto = notificationQueryDslRepository.getUserById(likerId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        TargetUserDto commentAuthorDto = notificationQueryDslRepository.getUserByCommentId(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        TargetPostDto targetPostDto = notificationQueryDslRepository.getPostByCommentId(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));

        return new CommentLikedContent(
                targetUserDto.nickname(),
                targetUserDto.profileUrl(),
                targetPostDto.imageUrl(),
                commentAuthorDto.id(),
                commentAuthorDto.nickname()
        );
    }
    public VotedContent forVoted(Long postId, Long voterId) {
        TargetUserDto targetUserDto = notificationQueryDslRepository.getUserById(voterId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        TargetUserDto postAuthorDto = notificationQueryDslRepository.getUserByPostId(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        TargetPostDto targetPostDto = notificationQueryDslRepository.getPostById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
        return new VotedContent(
                targetUserDto.nickname(),
                targetUserDto.profileUrl(),
                targetPostDto.imageUrl(),
                postAuthorDto.id(),
                postAuthorDto.nickname()
        );
    }
//    public NotificationContent forVoteClosed(Long postId) {
//        String title = postPort.getPostTitle(postId).orElse("투표 마감");
//        String body = "참여한 투표가 마감되었어요.";
//        String thumbnail = postPort.getPostThumbnailUrl(postId).orElse(null);
//        return new NotificationContent(title, body, thumbnail);
//    }
//
}
