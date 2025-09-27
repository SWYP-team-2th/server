package com.chooz.notification.application;

import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.application.web.dto.NotificationContent;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.Target;
import com.chooz.notification.domain.TargetType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationContentAssembler {

    private final NotificationService notificationService;
    private final NotificationMessageRenderer renderer;

    public NotificationContent forCommentLiked(Long commentId, Long likerId) {
        TargetUserDto commentAuthorDto = notificationService.findUserByCommentId(commentId);
        TargetUserDto targetUserDto = notificationService.findUserById(likerId);
        TargetPostDto targetPostDto = notificationService.findPostByCommentId(commentId);
        var vars = Map.<String, Object>of("actorName", targetUserDto.nickname());
        var renderedMessage = renderer.render(NotificationType.COMMENT_LIKED.code(), vars);
        return new NotificationContent(
                commentAuthorDto.id(),
                targetUserDto.id(),
                renderedMessage.title(),
                renderedMessage.content(),
                targetUserDto.profileUrl(),
                targetPostDto.imageUrl(),
                List.of(Target.of(targetPostDto.id(), TargetType.POST),
                        Target.of(targetPostDto.id(), TargetType.COMMENT)
                )
        );
    }
//    public NotificationContent forVoted(Long postId, Long voterId) {
//        TargetUserDto postAuthorDto = notificationQueryService.findUserByPostId(postId);
//        TargetUserDto targetUserDto = notificationQueryService.findUserById(voterId);
//        TargetPostDto targetPostDto = notificationQueryService.findPostById(postId);
//        return new NotificationContent(
//                postAuthorDto.id(),
//                targetUserDto.id(),
//                targetUserDto.nickname(),
//                targetUserDto.profileUrl(),
//                targetPostDto.imageUrl(),
//                List.of(Target.of(targetPostDto.id(), TargetType.POST))
//        );
//    }
//    public NotificationContent forPostClosed(Long postId) {
//        TargetUserDto postAuthorDto = notificationQueryDslRepository.getUserByPostId(postId)
//                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
//        TargetPostDto targetPostDto = notificationQueryDslRepository.getPostById(postId)
//                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));
//        return new PostClosedContent(
//                postAuthorDto.nickname(),
//                postAuthorDto.profileUrl(),
//                targetPostDto.imageUrl(),
//                postAuthorDto.id(),
//                postAuthorDto.nickname()
//        );
//    }
}
