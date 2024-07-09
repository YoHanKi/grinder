package com.grinder.domain.dto;

import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Tag;
import com.grinder.domain.enums.TagName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CafeDTO {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CafeRequestDTO {
        private String cafeId;
        private String name;
        private String address;
        private String phoneNum;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CafeResponseDTO {
        String cafeId;
        String name;
        String address;
        String phoneNum;
        Integer averageGrade;

        CafeResponseDTO(Cafe cafe) {
            this.cafeId = cafe.getCafeId();
            this.name = cafe.getName();
            this.address = cafe.getAddress();
            this.phoneNum = cafe.getPhoneNum();
            this.averageGrade = cafe.getAverageGrade();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CafeSearchByAdminDTO {
        private String name;
        private String address;
        private String phoneNum;

        public CafeSearchByAdminDTO(Cafe cafe) {
            this.name = cafe.getName();
            this.address = cafe.getAddress();
            this.phoneNum = cafe.getPhoneNum();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class findAllWithImageResponse {
        private String cafeId;
        private String cafeName;
        private String cafeAddress;
        private String cafePhoneNum;
        private Integer averageGrade;
        private String cafeImageUrl;


        public findAllWithImageResponse(Cafe cafe, String imageUrl) {
            cafeId = cafe.getCafeId();
            cafeName = cafe.getName();
            cafeAddress = cafe.getAddress();
            cafePhoneNum = cafe.getPhoneNum();
            averageGrade = cafe.getAverageGrade();
            cafeImageUrl = imageUrl;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class findAllWithImageAndTagResponse {
        private String cafeId;
        private String cafeName;
        private String cafeAddress;
        private String cafePhoneNum;
        private Integer averageGrade;
        private String cafeImageUrl;
        private List<String> tagList;


//        public findAllWithImageAndTagResponse(Cafe cafe, String imageUrl, List<Tag> tagList) {
//            cafeId = cafe.getCafeId();
//            cafeName = cafe.getName();
//            cafeAddress = cafe.getAddress();
//            cafePhoneNum = cafe.getPhoneNum();
//            averageGrade = cafe.getAverageGrade();
//            cafeImageUrl = imageUrl;
//            List<String> list = new ArrayList<>();
//            for (Tag tag : tagList) {
//                list.add(tag.getTagName().getValue());
//            }
//            this.tagList = list;
//        }

        public findAllWithImageAndTagResponse(findAllWithImageResponse cafe, List<TagName> tagList) {
            cafeId = cafe.getCafeId();
            cafeName = cafe.getCafeName();
            if (cafe.getCafeAddress().contains(" ")) {
                cafeAddress = cafe.getCafeAddress().split(" ")[0] + " " + cafe.getCafeAddress().split(" ")[1];
            } else cafeAddress = cafe.getCafeAddress();
            cafePhoneNum = cafe.getCafePhoneNum();
            averageGrade = cafe.getAverageGrade();
            cafeImageUrl = cafe.cafeImageUrl;
            List<String> list = new ArrayList<>();
            for (TagName tag : tagList) {
                list.add(tag.getValue());
            }
            this.tagList = list;
        }
    }
}
