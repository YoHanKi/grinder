package com.grinder.domain.dto;

import com.grinder.domain.entity.CafeRegister;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class CafeRegisterDTO {

    @Getter
    @NoArgsConstructor
    public static class CafeRegisterRequestDTO {

        private String name;
        private String address;
        private String phoneNum;

        public CafeRegisterRequestDTO(CafeRegister cafeRegister) {
            this.name = cafeRegister.getName();
            this.address = cafeRegister.getAddress();
            this.phoneNum = cafeRegister.getPhoneNum();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FindCafeRegisterDTO {

        private String registerId;
        private String nickname;
        private String cafeName;
        private String address;
        private String phoneNum;

        public FindCafeRegisterDTO(CafeRegister cafeRegister) {
            this.registerId = cafeRegister.getRegisterId();
            this.nickname = cafeRegister.getMember().getNickname();
            this.cafeName = cafeRegister.getName();
            this.address = cafeRegister.getAddress();
            this.phoneNum = cafeRegister.getPhoneNum().substring(0,3) + "-" + cafeRegister.getPhoneNum().substring(3,7) + "-" + cafeRegister.getPhoneNum().substring(7,11);
        }
    }

//    @Getter
//    @Setter
//    public static class SaveCafeRegisterDTO {
//
//        private String registerId;
//        private String memberId;
//        private String name;
//        private String address;
//        private String phoneNum;
//
//        public SaveCafeRegisterDTO(CafeRegister cafeRegister) {
//            this.registerId = cafeRegister.getRegisterId();
//            this.memberId = cafeRegister.getMember().getMemberId();
//            this.name = cafeRegister.getName();
//            this.address = cafeRegister.getAddress();
//            this.phoneNum = cafeRegister.getPhoneNum();
//        }
//    }
}
