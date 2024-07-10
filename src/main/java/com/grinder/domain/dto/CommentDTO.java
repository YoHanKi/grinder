package com.grinder.domain.dto;

import com.grinder.domain.entity.Comment;
import com.grinder.domain.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentRequestDTO {
        private String content;
        private String parentCommentId;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ParentCommentResponseDTO {
        private String commentId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String nickname;
        private String email;
        private String memberId;
        private String feedId;
        private List<CommentDTO.ChildCommentResponseDTO> childCommentList;
        private boolean isHeart;    // 사용자가 댓글을 좋아요 했는지 여부
        private int heartNum;       // 해당 댓글의 좋아요 수
        private String memberImage;

//        public ParentCommentResponseDTO(Comment comment) {
//            this.commentId = comment.getCommentId();
//            this.content = comment.getContent();
//            this.createdAt = comment.getCreatedAt();
//            this.updatedAt = comment.getUpdatedAt();
//            this.nickname = comment.getMember().getNickname();
//            this.email = comment.getMember().getEmail();
//            this.memberId = comment.getMember().getMemberId();
//            this.feedId = comment.getFeed().getFeedId();
//            this.isHeart = false;
//            this.heartNum = 0;
//        }

        public ParentCommentResponseDTO(Comment comment, List<CommentDTO.ChildCommentResponseDTO> childCommentList, Boolean isHeart, Long heartNum, String memberImage) {
            this.commentId = comment.getCommentId();
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
            this.updatedAt = comment.getUpdatedAt();
            this.nickname = comment.getMember().getNickname();
            this.email = comment.getMember().getEmail();
            this.memberId = comment.getMember().getMemberId();
            this.feedId = comment.getFeed().getFeedId();
            this.childCommentList = childCommentList;
            this.isHeart = isHeart;
            this.heartNum = Math.toIntExact(heartNum);
            this.memberImage = memberImage;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChildCommentResponseDTO {
        private String commentId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String nickname;
        private String email;
        private String memberId;
        private String memberImage;
        private String feedId;
        private String parentCommentId;
        private boolean isHeart;    // 사용자가 댓글을 좋아요 했는지 여부
        private int heartNum;       // 해당 댓글의 좋아요 수

//        public ChildCommentResponseDTO(Comment comment) {
//            this.commentId = comment.getCommentId();
//            this.content = comment.getContent();
//            this.createdAt = comment.getCreatedAt();
//            this.updatedAt = comment.getUpdatedAt();
//            this.nickname = comment.getMember().getNickname();
//            this.email = comment.getMember().getEmail();
//            this.memberId = comment.getMember().getMemberId();
//            this.feedId = comment.getFeed().getFeedId();
//            this.parentCommentId = comment.getParentComment().getCommentId();
//            this.isHeart = false;
//            this.heartNum = 0;
//        }

        public ChildCommentResponseDTO(Comment comment, Boolean isHeart, Long heartNum, String memberImage) {
            this.commentId = comment.getCommentId();
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
            this.updatedAt = comment.getUpdatedAt();
            this.nickname = comment.getMember().getNickname();
            this.email = comment.getMember().getEmail();
            this.memberId = comment.getMember().getMemberId();
            this.feedId = comment.getFeed().getFeedId();
            this.parentCommentId = comment.getParentComment().getCommentId();
            this.isHeart = isHeart;
            this.heartNum = Math.toIntExact(heartNum);
            this.memberImage = memberImage;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindCommentDTO {
        private String commentId;
        private String content;
        private String nickname;

        public FindCommentDTO(Comment comment, Member member) {
            this.commentId = comment.getCommentId();
            this.content = comment.getContent();
            this.nickname = member.getNickname();
        }
    }
}
