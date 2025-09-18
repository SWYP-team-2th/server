package com.chooz.notification.application;

import com.chooz.common.exception.BadRequestException;
import com.chooz.common.exception.ErrorCode;
import com.chooz.notification.application.dto.CommentLikedContent;
import com.chooz.notification.application.port.PostReadPort;
import com.chooz.notification.application.port.UserReadPort;
import com.chooz.notification.application.port.view.PostView;
import com.chooz.notification.application.port.view.UserView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationContentAssembler {

    private final UserReadPort userReadPort;
    private final PostReadPort postReadPort;

    public CommentLikedContent forCommentLiked(Long commentId, Long likerId) {
        UserView likerUserView = userReadPort.getUser(likerId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        UserView commentAuthorView = userReadPort.getUserByCommentId(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
        PostView postView = postReadPort.getPost(commentId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.POST_NOT_FOUND));

        return new CommentLikedContent(
                likerUserView.nickname() + " 님이 당신의 댓글에 좋아요를 눌렀어요!",
                "지금 확인해보세요.",
                postView.imageUrl(),
                likerUserView.profileUrl(),
                commentAuthorView.id()
        );
    }

//    public NotificationContent forVoteClosed(Long postId) {
//        String title = postPort.getPostTitle(postId).orElse("투표 마감");
//        String body = "참여한 투표가 마감되었어요.";
//        String thumbnail = postPort.getPostThumbnailUrl(postId).orElse(null);
//        return new NotificationContent(title, body, thumbnail);
//    }
//
//    public NotificationContent forPostParticipated(Long postId, Long voterId) {
//        String title = postPort.getPostTitle(postId).orElse("새로운 참여");
//        String voter = userPort.getDisplayName(voterId).orElse("누군가");
//        String body = voter + "님이 내 투표에 참여했어요.";
//        String thumbnail = userPort.getAvatarUrl(voterId).orElse(null);
//        return new NotificationContent(title, body, thumbnail);
//    }
}
