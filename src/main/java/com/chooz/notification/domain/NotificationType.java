package com.chooz.notification.domain;

public enum NotificationType {
    POST_CLOSED("NOTI.POST.CLOSED"),
    MY_POST_CLOSED("NOTI.MY.POST.CLOSED"),
    COMMENT_LIKED("NOTI.COMMENT.LIKED"),
    POST_VOTED("NOTI.POST.VOTED");

    private final String code;
    NotificationType(String code) {this.code = code;}
    public String code() {return code;}
}
