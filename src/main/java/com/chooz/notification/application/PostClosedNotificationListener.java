//package com.chooz.notification.application;
//
//import com.chooz.notification.domain.Notification;
//import com.chooz.notification.domain.TargetType;
//import com.chooz.notification.domain.event.PostClosedNotificationEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//@Component
//@RequiredArgsConstructor
//public class PostClosedNotificationListener {
//
//    private final NotificationCommandService notificationCommandService;
//    private final NotificationContentAssembler notificationContentAssembler;
//
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onVoted(PostClosedNotificationEvent e) {
//        PostClosedContent postClosedContent = notificationContentAssembler.forPostClosed(e.postId());
//        Notification.create(
//                postClosedContent.getPostAuthorId(),
//                postClosedContent.getPostAuthorName(),
//                postClosedContent.getPostAuthorId(),
//                postClosedContent.getActorName(),
//                postClosedContent.getActorProfileImageUrl(),
//                e.postId(),
//                TargetType.POST,
//                postClosedContent.getTargetThumbnailUrl(),
//                e.eventAt()
//        ).ifPresent(notificationCommandService::create);
//    }
//}
