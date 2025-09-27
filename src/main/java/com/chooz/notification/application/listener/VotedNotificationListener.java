//package com.chooz.notification.application;
//
//import com.chooz.notification.domain.Notification;
//import com.chooz.notification.domain.TargetType;
//import com.chooz.notification.domain.event.VotedNotificationEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//@Component
//@RequiredArgsConstructor
//public class VotedNotificationListener {
//
//    private final NotificationCommandService notificationCommandService;
//    private final NotificationContentAssembler notificationContentAssembler;
//
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onVoted(VotedNotificationEvent e) {
//        VotedContent votedContent = notificationContentAssembler.forVoted(e.postId(), e.voterId());
//        Notification.create(
//                votedContent.getPostAuthorId(),
//                votedContent.getPostAuthorName(),
//                e.voterId(),
//                votedContent.getActorName(),
//                votedContent.getActorProfileImageUrl(),
//                e.postId(),
//                TargetType.VOTE,
//                votedContent.getTargetThumbnailUrl(),
//                e.eventAt()
//        ).ifPresent(notificationCommandService::create);
//    }
//}
