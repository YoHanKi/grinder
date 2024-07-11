package com.grinder.domain.dto;

import com.grinder.domain.entity.Bookmark;
import com.grinder.domain.entity.SellerInfo;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Generated
public class SellerInfoDTO {

    @Getter
    @Setter
    public static class findAllResponse {
        private String cafeId;
        private String cafeName;
        private String cafeAddress;
        private String CafePhoneNum;
        private Integer averageGrade;
        private String cafeImageUrl;

        public findAllResponse() {
        }

        public findAllResponse(SellerInfo sellerInfo, String imageUrl) {
            cafeId = sellerInfo.getCafe().getCafeId();
            cafeName = sellerInfo.getCafe().getName();
            cafeAddress = sellerInfo.getCafe().getAddress();
            CafePhoneNum = sellerInfo.getCafe().getPhoneNum();
            averageGrade = sellerInfo.getCafe().getAverageGrade();
            cafeImageUrl = imageUrl;
        }
    }
}
