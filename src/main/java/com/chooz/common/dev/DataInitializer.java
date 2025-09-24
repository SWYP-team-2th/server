package com.chooz.common.dev;

import com.chooz.auth.application.jwt.JwtService;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.post.domain.CloseOption;
import com.chooz.post.domain.CloseType;
import com.chooz.post.domain.CommentActive;
import com.chooz.post.domain.PollChoice;
import com.chooz.post.domain.PollOption;
import com.chooz.post.domain.PollType;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.domain.Scope;
import com.chooz.user.domain.NicknameAdjectiveRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.application.VoteService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Profile({"!prod", "!test"})
@Component
public class DataInitializer {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;
    private final VoteService voteService;
    private final CommentRepository commentRepository;

    public DataInitializer(
            NicknameAdjectiveRepository nicknameAdjectiveRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            JwtService jwtService,
            VoteService voteService,
            CommentRepository commentRepository
    ) {
        this.nicknameAdjectiveRepository = nicknameAdjectiveRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.jwtService = jwtService;
        this.voteService = voteService;
        this.commentRepository = commentRepository;
    }


    @Transactional
    public void init() {
        User user = userRepository.save(User.create("chooz1", "https://t1.kakaocdn.net/account_images/default_profile.jpeg"));
        User user2 = userRepository.save(User.create("chooz2", "https://t1.kakaocdn.net/account_images/default_profile.jpeg"));
        postRepository.save(Post.create(
                user.getId(),
                "title",
                "description",
                "imageUrl",
                List.of(PollChoice.create("title1", "imageUrl1"), PollChoice.create("title1", "imageUrl1")),
                "shareUrl",
                PollOption.create(PollType.SINGLE, Scope.PUBLIC, CommentActive.OPEN),
                CloseOption.create(CloseType.VOTER, null, 2)));
        postRepository.save(Post.create(
                user.getId(),
                "title",
                "description",
                "imageUrl",
                List.of(PollChoice.create("title1", "imageUrl1"), PollChoice.create("title1", "imageUrl1")),
                "shareUrl",
                PollOption.create(PollType.SINGLE, Scope.PUBLIC, CommentActive.OPEN),
                new CloseOption(CloseType.DATE, LocalDateTime.now().plusMinutes(5), null)));
//        TokenResponse tokenResponse = jwtService.createToken(new JwtClaim(testUser.getId(), testUser.getRole()));
//        TokenPair tokenPair = tokenResponse.tokenPair();
//        System.out.println("accessToken = " + tokenPair.accessToken());
//        System.out.println("refreshToken = " + tokenPair.refreshToken());
//        List<User> users = new ArrayList<>();
//        List<Post> posts = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            String userName = adjectives.size() < 10 ? "user" + i : adjectives.get(i).getAdjective();
//            User user = userRepository.save(User.create(userName, "https://t1.kakaocdn.net/account_images/default_profile.jpeg"));
//            users.add(user);
//            for (int j = 0; j < 30; j += 2) {
//                ImageFile imageFile1 = imageFileRepository.save(ImageFile.create(new ImageFileDto("202502240006030.png", "https://image.chooz.site/images-dev/202502240006030.png", "https://image.chooz.site/images-dev/resized_202502240006030.png")));
//                ImageFile imageFile2 = imageFileRepository.save(ImageFile.create(new ImageFileDto("202502240006030.png", "https://image.chooz.site/images-dev/202502240006030.png", "https://image.chooz.site/images-dev/resized_202502240006030.png")));
//                Post post = postRepository.save(Post.create(
//                        user.getId(),
//                        "title" + j,
//                        "description" + j,
//                        List.of(PollChoice.create("뽀또A", imageFile1.getId()), PollChoice.create("뽀또B", imageFile2.getId())),
//                        PollOption.create(PollType.SINGLE, Scope.PUBLIC, CommentActive.OPEN),
//                        CloseOption.create(CloseType.SELF, null, null)
//                ));
//                posts.add(post);
//            }
//
//        }
//        for (User user : users) {
//            for (Post post : posts) {
//                Random random = new Random();
//                int num = random.nextInt(2);
//                voteService.vote(user.getId(), post.getId(), post.getPollChoices().get(num).getId());
//                commentRepository.save(new Comment(post.getId(), user.getId(), "댓글 내용" + random.nextInt(100)));
//            }
//        }
    }
}
