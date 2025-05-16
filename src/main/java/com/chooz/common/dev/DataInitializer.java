package com.chooz.common.dev;

import com.chooz.auth.application.jwt.JwtClaim;
import com.chooz.auth.application.jwt.JwtService;
import com.chooz.auth.application.jwt.TokenPair;
import com.chooz.auth.presentation.dto.TokenResponse;
import com.chooz.comment.domain.Comment;
import com.chooz.comment.domain.CommentRepository;
import com.chooz.post.application.ShareUrlService;
import com.chooz.image.domain.ImageFile;
import com.chooz.image.domain.ImageFileRepository;
import com.chooz.image.presentation.dto.ImageFileDto;
import com.chooz.post.domain.Post;
import com.chooz.post.domain.PostImage;
import com.chooz.post.domain.PostRepository;
import com.chooz.post.domain.Scope;
import com.chooz.post.domain.VoteType;
import com.chooz.user.domain.NicknameAdjective;
import com.chooz.user.domain.NicknameAdjectiveRepository;
import com.chooz.user.domain.User;
import com.chooz.user.domain.UserRepository;
import com.chooz.vote.application.VoteService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Profile({"dev", "local"})
@Component
public class DataInitializer {

    private final NicknameAdjectiveRepository nicknameAdjectiveRepository;
    private final UserRepository userRepository;
    private final ImageFileRepository imageFileRepository;
    private final PostRepository postRepository;
    private final ShareUrlService shaereUrlShareUrlService;
    private final JwtService jwtService;
    private final VoteService voteService;
    private final CommentRepository commentRepository;

    public DataInitializer(
            NicknameAdjectiveRepository nicknameAdjectiveRepository,
            UserRepository userRepository,
            ImageFileRepository imageFileRepository,
            PostRepository postRepository,
            ShareUrlService shaereUrlShareUrlService,
            JwtService jwtService,
            VoteService voteService,
            CommentRepository commentRepository
    ) {
        this.nicknameAdjectiveRepository = nicknameAdjectiveRepository;
        this.userRepository = userRepository;
        this.imageFileRepository = imageFileRepository;
        this.postRepository = postRepository;
        this.shaereUrlShareUrlService = shaereUrlShareUrlService;
        this.jwtService = jwtService;
        this.voteService = voteService;
        this.commentRepository = commentRepository;
    }


    @Transactional
    public void init() {
        if (userRepository.count() > 0) {
            return;
        }
        List<NicknameAdjective> adjectives = nicknameAdjectiveRepository.findAll();
        User testUser = userRepository.save(User.create("nickname", "https://t1.kakaocdn.net/account_images/default_profile.jpeg"));
        TokenResponse tokenResponse = jwtService.createToken(new JwtClaim(testUser.getId(), testUser.getRole()));
        TokenPair tokenPair = tokenResponse.tokenPair();
        System.out.println("accessToken = " + tokenPair.accessToken());
        System.out.println("refreshToken = " + tokenPair.refreshToken());
        List<User> users = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String userName = adjectives.size() < 10 ? "user" + i : adjectives.get(i).getAdjective();
            User user = userRepository.save(User.create(userName, "https://t1.kakaocdn.net/account_images/default_profile.jpeg"));
            users.add(user);
            for (int j = 0; j < 30; j += 2) {
                ImageFile imageFile1 = imageFileRepository.save(ImageFile.create(new ImageFileDto("202502240006030.png", "https://image.chooz.site/images-dev/202502240006030.png", "https://image.chooz.site/images-dev/resized_202502240006030.png")));
                ImageFile imageFile2 = imageFileRepository.save(ImageFile.create(new ImageFileDto("202502240006030.png", "https://image.chooz.site/images-dev/202502240006030.png", "https://image.chooz.site/images-dev/resized_202502240006030.png")));
                Post post = postRepository.save(Post.create(user.getId(), "description" + j, List.of(PostImage.create("뽀또A", imageFile1.getId()), PostImage.create("뽀또B", imageFile2.getId())), Scope.PUBLIC, VoteType.SINGLE));
                post.setShareUrl(shaereUrlShareUrlService.encrypt(String.valueOf(post.getId())));
                posts.add(post);
            }

        }
        for (User user : users) {
            for (Post post : posts) {
                Random random = new Random();
                int num = random.nextInt(2);
                voteService.vote(user.getId(), post.getId(), post.getImages().get(num).getId());
                commentRepository.save(new Comment(post.getId(), user.getId(), "댓글 내용" + random.nextInt(100)));
            }
        }
    }
}
