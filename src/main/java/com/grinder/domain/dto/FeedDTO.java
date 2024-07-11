package com.grinder.domain.dto;

import com.grinder.domain.entity.*;
import com.grinder.service.ImageService;
import com.grinder.service.TagService;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedDTO {


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedRequestDTO {
        // 피드 내용, 이미지, 카페, 평점, 태그
        private String cafeId;
        private String content;
        private List<String> tagNameList;
        private Integer grade;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewFeedResponseDTO {
        private String feedId;
        private String content;
        private List<String> imageUrlList;
        private CafeDTO.CafeResponseDTO cafe;
        private Integer grade;
        private List<String> tagNameList;

        public NewFeedResponseDTO(Feed feed, List<Image> imageList, List<Tag> tagNameList) {
            this.feedId = feed.getFeedId();
            this.content = feed.getContent();
            this.imageUrlList = imageList == null ? new ArrayList<>() : imageList.stream().map(Image::getImageUrl).toList();
            this.cafe = feed.getCafe() == null? new CafeDTO.CafeResponseDTO() :  new CafeDTO.CafeResponseDTO(feed.getCafe());
            this.grade = feed.getGrade();
            this.tagNameList = tagNameList == null? new ArrayList<>() : tagNameList.stream().map(x->x.getTagName().toString()).toList();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindFeedDTO {
        private String feedId;
        private String nickname;
        private String name;
        private String content;
        private Integer grade;
        private List<String> imageUrls;
        private List<String> tagNames;

        public FindFeedDTO(Feed feed, Member member, Cafe cafe, List<String> imageUrls, List<String> tagNames) {
            this.feedId = feed.getFeedId();
            this.nickname = member.getNickname();
            this.name = cafe.getName();
            this.content = feed.getContent();
            this.grade = feed.getGrade();
            this.imageUrls = imageUrls;
            this.tagNames = tagNames;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class FeedWithImageResponseDTO {
        private String feedId;
        private String memberNickname;
        private String memberEmail;
        private String memberImage;
        private CafeDTO.CafeResponseDTO cafe;
        private String content;
        private Boolean isVisible;
        private Integer grade;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> tagNameList;
        private List<CommentDTO.ParentCommentResponseDTO> parentCommentList;
        private List<String> imageUrls;
        private boolean isHeart;    // 사용자가 댓글을 좋아요 했는지 여부
        private int heartNum;       // 해당 댓글의 좋아요 수


        public FeedWithImageResponseDTO(Feed feed, List<Tag> tagNameList ,List<CommentDTO.ParentCommentResponseDTO> parentCommentList, List<String> imageUrls, Boolean isHeart, Long heartNum, String memberImage) {
            this.feedId = feed.getFeedId();
            this.memberNickname = feed.getMember().getNickname();
            this.memberEmail = feed.getMember().getEmail();
            this.memberImage = memberImage;
            this.cafe = feed.getCafe() == null? new CafeDTO.CafeResponseDTO() :  new CafeDTO.CafeResponseDTO(feed.getCafe());
            this.content = feed.getContent();
            this.isVisible = feed.getIsVisible();
            this.grade = feed.getGrade();
            this.createdAt = feed.getCreatedAt();
            this.updatedAt = feed.getUpdatedAt();
            //태그처리
            List<String> list = new ArrayList<>();
            for (Tag tag : tagNameList) list.add(tag.getTagName().getValue());
            this.tagNameList = list;
            this.parentCommentList = parentCommentList;
            this.imageUrls = imageUrls;
            this.isHeart = isHeart;
            this.heartNum = Math.toIntExact(heartNum);
        }

        public FeedWithImageResponseDTO(Feed feed, List<String> imageUrls, Long heartNum) {
            this.isVisible = feed.getIsVisible();
            this.imageUrls = imageUrls;
            this.heartNum = Math.toIntExact(heartNum);
        }
    }
}
