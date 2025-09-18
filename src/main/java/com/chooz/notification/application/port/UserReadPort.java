package com.chooz.notification.application.port;

import com.chooz.notification.application.port.view.UserView;
import java.util.Optional;

public interface UserReadPort {
    Optional<UserView> getUserByCommentId(Long commentId);
    Optional<UserView> getUser(Long userId);
}
