package com.chooz.notification.application.port;

import com.chooz.notification.application.port.view.PostView;
import java.util.Optional;

public interface PostReadPort {
    Optional<PostView> getPost(Long commentId);
}
