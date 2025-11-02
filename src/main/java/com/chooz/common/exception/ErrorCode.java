package com.chooz.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //400
    USER_NOT_FOUND("회원정보를 찾을 수 없어요."),
    INVALID_ARGUMENT("요청이 잘못되었어요. 다시 시도해주세요."),
    REFRESH_TOKEN_MISMATCHED("로그인 정보가 만료됐어요. 다시 로그인 해주세요."),
    REFRESH_TOKEN_NOT_FOUND("로그인 세션이 만료됐어요. 다시 로그인 해주세요."),
    INVALID_REFRESH_TOKEN_HEADER("로그인 정보가 올바르지 않아요. 다시 로그인 해주세요."),
    MISSING_FILE_EXTENSION("업로드 파일 형식을 확인해주세요."),
    UNSUPPORTED_IMAGE_EXTENSION("업로드할 수 있는 확장자가 아니에요."),
    EXCEED_MAX_FILE_SIZE("파일 용량이 너무 커요."),
    POST_NOT_FOUND("게시글을 찾을 수 없어요."),
    DESCRIPTION_LENGTH_EXCEEDED("설명은 100자 이내로 입력해주세요."),
    TITLE_IS_REQUIRED("제목을 입력해주세요."),
    TITLE_LENGTH_EXCEEDED("제목은 50자 이내로 입력해주세요."),
    INVALID_POLL_CHOICE_COUNT("선택지는 최소 2개, 최대 10개까지 등록할 수 있어요."),
    POLL_CHOICE_TITLE_LENGTH_EXCEEDED("선택지 이름은 10자 이내로 입력해주세요."),
    NOT_POST_AUTHOR("본인이 작성한 게시글만 수정하거나 삭제할 수 있어요."),
    POST_ALREADY_CLOSED("이미 마감된 투표예요. 결과를 확인해보세요."),
    FILE_NAME_TOO_LONG("파일 이름이 너무 길어요. 짧게 수정해주세요."),
    ACCESS_DENIED_VOTE_STATUS("아직 투표 현황을 조회할 수 없어요."),
    COMMENT_NOT_FOUND("댓글을 찾을 수 없어요."),
    VOTE_NOT_FOUND("투표를 찾을 수 없어요."),
    NOT_VOTER("투표에 참여하지 않았어요."),
    CLOSED_AT_REQUIRED("마감 시간을 설정해주세요."),
    MAX_VOTER_COUNT_REQUIRED("참여 인원 제한을 설정해주세요."),
    INVALID_VOTER_CLOSE_OPTION("투표자 수 마감 설정이 올바르지 않아요."),
    INVALID_DATE_CLOSE_OPTION("마감시간이 올바르지 않아요. 다시 선택해주세요."),
    INVALID_SELF_CLOSE_OPTION("마감 옵션이 잘못됐어요. 다시 선택해주세요."),
    INVALID_CLOSE_OPTION("마감방식이 올바르지 않아요."),
    THUMBNAIL_NOT_FOUND("미리보기 이미지를 불러올 수 없어요."),
    CLOSE_DATE_OVER("이미 마감된 투표에요."),
    EXCEED_MAX_VOTER_COUNT("이미 투표인원이 가득 찼어요."),
    CLOSE_COMMENT_ACTIVE("현재 댓글 기능이 꺼져있어요."),
    COMMENT_NOT_BELONG_TO_POST("댓글 정보가 올바르지 않아요."),
    NOT_COMMENT_AUTHOR("본인이 작성한 댓글만 수정할 수 있어요."),
    COMMENT_LENGTH_OVER("댓글은 200자 이내로 작성해주세요."),
    COMMENT_LIKE_NOT_FOUND("좋아요 정보를 찾을 수 없어요."),
    NOT_COMMENT_LIKE_AUTHOR("본인이 누른 좋아요만 취소할 수 있어요."),
    SINGLE_POLL_ALLOWS_MAXIMUM_ONE_CHOICE("단일 투표는 한가지 선택지만 가능해요."),
    DUPLICATE_POLL_CHOICE("같은 선택지는 중복으로 투표할 수 없어요."),
    NOT_POST_POLL_CHOICE_ID("올바르지 않은 투표항목이에요."),
    ONLY_SELF_CAN_CLOSE("직접 마감은 작성자만 할 수 있어요."),
    INVALID_ONBOARDING_STEP("잘못된 온보딩 단계로 이동했어요."),
    NICKNAME_LENGTH_EXCEEDED("닉네임은 15자 이내로 입력해주세요."),
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없어요."),
    POST_NOT_REVEALABLE("이 게시글은 특정 사용자에게만 공개돼 있어요."),

    //401
    EXPIRED_TOKEN("로그인 세션이 만료됐어요. 다시 로그인 해주세요."),
    INVALID_TOKEN("로그인 정보가 유효하지 않아요. 다시 로그인 해주세요."),
    INVALID_AUTH_HEADER("로그인 정보가 손상됐어요. 다시 로그인 해주세요."),

    //403
    FORBIDDEN("접근 권한이 없어요. 로그인 후 다시 시도해주세요."),

    //404
    NOT_FOUND("페이지를 찾을 수 없어요. 주소를 다시 확인 해주세요."),

    //500
    INTERNAL_SERVER_ERROR("잠시 문제가 발생했어요. 잠시 후 다시 시도해주세요."),
    INVALID_INPUT_VALUE("입력 값을 다시 확인해주세요."),
    SOCIAL_AUTHENTICATION_FAILED("소셜 로그인이 실패했어요."),
    POLL_CHOICE_NAME_GENERATOR_INDEX_OUT_OF_BOUND("이미지 등록 중 오류가 발생했어요."),
    IMAGE_FILE_NOT_FOUND("이미지를 찾을 수 없어요."),
    POLL_CHOICE_NOT_FOUND("투표항목을 찾을 수 없어요."),
    SHARE_URL_ALREADY_EXISTS("이미 공유된 링크예요."),

    //503
    SERVICE_UNAVAILABLE("잠시 점검 중이에요. 조금만 기다려주세요.🙏"),
    ;

    private final String message;
}
