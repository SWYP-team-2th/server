package com.chooz.notification.application;

import com.chooz.notification.application.dto.TargetPostDto;
import com.chooz.notification.application.dto.TargetUserDto;
import com.chooz.notification.application.dto.NotificationContent;
import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.Target;
import com.chooz.notification.domain.TargetType;
import com.chooz.post.domain.CloseType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                        Target.of(commentId, TargetType.COMMENT)
                )
        );
    }
    public NotificationContent forVoted(Long postId, Long voterId) {
        TargetUserDto postAuthorDto = notificationService.findUserByPostId(postId);
        TargetUserDto targetUserDto = notificationService.findUserById(voterId);
        TargetPostDto targetPostDto = notificationService.findPostById(postId);
        var vars = Map.<String, Object>of(
                "actorName", targetUserDto.nickname(),
                "postTitle", targetPostDto.title()
                );
        var renderedMessage = renderer.render(NotificationType.POST_VOTED.code(), vars);
        return new NotificationContent(
                postAuthorDto.id(),
                targetUserDto.id(),
                renderedMessage.title(),
                renderedMessage.content(),
                targetUserDto.profileUrl(),
                targetPostDto.imageUrl(),
                List.of(Target.of(targetPostDto.id(), TargetType.POST))
        );
    }
    public NotificationContent forMyPostClosed(Long postId, Long userId) {
        TargetUserDto postAuthorDto = notificationService.findUserById(userId);
        TargetPostDto targetPostDto = notificationService.findPostById(postId);
        var vars = Map.<String, Object>of(
                "postTitle", targetPostDto.title()
        );
        var renderedMessage = renderer.render(NotificationType.MY_POST_CLOSED.code(), vars);
        return new NotificationContent(
                postAuthorDto.id(),
                postAuthorDto.id(),
                renderedMessage.title(),
                renderedMessage.content(),
                postAuthorDto.profileUrl(),
                targetPostDto.imageUrl(),
                List.of(Target.of(targetPostDto.id(), TargetType.POST))
        );
    }
    public List<NotificationContent> forPostClosed(Long postId, Long userId) {
        TargetUserDto postAuthorDto = notificationService.findUserById(userId); //actor
        List<TargetUserDto> receiverUserDtos = notificationService.findVoteUsersByPostId(postId); //receiver
        TargetPostDto targetPostDto = notificationService.findPostById(postId);
        var vars = Map.<String, Object>of(
                "postTitle", targetPostDto.title()
        );
        var renderedMessage = renderer.render(NotificationType.POST_CLOSED.code(), vars);
        return receiverUserDtos.stream().map(receiver ->
                new NotificationContent(
                        receiver.id(),
                        postAuthorDto.id(),
                        renderedMessage.title(),
                        renderedMessage.content(),
                        postAuthorDto.profileUrl(),
                        targetPostDto.imageUrl(),
                        List.of(Target.of(targetPostDto.id(), TargetType.POST))
                )).collect(Collectors.toList());
    }
}
