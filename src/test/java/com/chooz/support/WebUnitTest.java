package com.chooz.support;

import com.chooz.image.application.ImageService;
import com.chooz.notification.application.service.NotificationQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.chooz.auth.application.AuthService;
import com.chooz.auth.presentation.RefreshTokenCookieGenerator;
import com.chooz.comment.application.CommentService;
import com.chooz.commentLike.application.CommentLikeService;
import com.chooz.common.exception.DiscordMessageSender;
import com.chooz.post.application.PostService;
import com.chooz.user.application.UserService;
import com.chooz.vote.application.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import(RefreshTokenCookieGenerator.class)
public abstract class WebUnitTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RefreshTokenCookieGenerator refreshTokenCookieGenerator;

    @MockitoBean
    protected ImageService imageService;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected PostService postService;

    @MockitoBean
    protected VoteService voteService;

    @MockitoBean
    protected CommentService commentService;

    @MockitoBean
    protected CommentLikeService commentLikeService;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected DiscordMessageSender discordMessageSender;

    @MockitoBean
    protected NotificationQueryService notificationQueryService;
}
