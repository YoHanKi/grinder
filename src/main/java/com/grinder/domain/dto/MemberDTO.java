package com.grinder.domain.dto;

import com.grinder.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FindMemberDTO {

        private String memberId;
        private String email;
        private String nickname;
        private String phoneNum;
        private String role;
        private Boolean isDeleted;

        public FindMemberDTO(Member member) {
            this.memberId = member.getMemberId();
            this.email = member.getEmail();
            this.nickname = member.getNickname();
            this.phoneNum = member.getPhoneNum();
            this.role = member.getRole().name();
            this.isDeleted = member.getIsDeleted();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRequestDto {
        private String email;
        private String nickname;
        private String password;
        private String phoneNum;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FindMemberAndImageDTO {

        private String memberId;
        private String email;
        private String nickname;
        private String role;
        private String image;

        public FindMemberAndImageDTO(Member member, String image) {
            memberId = member.getMemberId();
            email = member.getEmail();
            nickname = member.getNickname();
            role = member.getRole().getValue();
            this.image = image;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberUpdateRequestDto {
        private String memberId;
        private String nowPassword;
        private String nickname;
        private String password;
        private String phoneNum;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchMemberDTO {
        private String followId;
        private String followMemberId;
        private String followNickname;
        private String followEmail;
        private String followRole;
        private String followImage;

        public SearchMemberDTO(String memberId, FindMemberAndImageDTO dto) {
            this.followId = memberId;
            this.followMemberId = dto.getMemberId();
            this.followNickname = dto.getNickname();
            this.followEmail = dto.getEmail();
            this.followRole = dto.getRole();
            this.followImage = dto.getImage();
        }
    }
}
