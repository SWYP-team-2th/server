package com.chooz.notification.domain;

public enum NotificationType {
    POST_CLOSED("NOTI.POST.CLOSED"),
    MY_POST_CLOSED("NOTI.MY.POST.CLOSED"),
    COMMENT_LIKED("NOTI.COMMENT.LIKED"),
    POST_VOTED("NOTI.POST.VOTED");

    private final String code;
    NotificationType(String code) {this.code = code;}
    public String code() {return code;}

    public static boolean isMyPostClosed(NotificationType notificationType) {
        return NotificationType.valueOf(notificationType.name()).equals(MY_POST_CLOSED);
    }
}
