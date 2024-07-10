package com.grinder.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TagName {
    CLEAN("🧹매장이 깨끗해요"),
    KIND("💌직원들이 친절해요"),
    GOOD_INTERIOR("✨인테리어가 좋아요"),
    GOOD_COFFEE("☕커피가 맛있어요"),
    MANY_SEATS("🪑자리가 많아요"),
    GOOD_DESSERT("🍮디저트가 맛있어요"),
    GOOD_VIEW("🌳풍경이 좋아요"),
    FAST_WIFI("📶와이파이가 빨라요"),
    PARKING_AVAILABLE("🚗주차 가능해요"),
    PET_FRIENDLY("🐾애완동물 동반 가능해요"),
    GOOD_MUSIC("🎵음악이 좋아요"),
    FAMILY_FRIENDLY("👨‍👩‍👧가족 단위 방문에 좋아요"),
    GOOD_FOR_GROUPS("👥단체 손님에 적합해요"),
    QUIET("🔇조용해요"),
    ACCESSIBLE("♿휠체어 사용 가능해요"),
    GOOD_FOR_DATES("💑데이트하기 좋아요"),
    HEALTHY_OPTIONS("🥗건강식 옵션이 많아요"),
    OPEN_LATE("🌜늦게까지 영업해요"),
    HAS_TERRACE("🏞테라스가 있어요"),
    HAS_TV("📺TV가 설치되어 있어요"),
    SMOKING_AREA("🚬흡연 구역이 있어요");

    private final String value;

//    public static TagName fromString(String text) {
//        for (TagName type : TagName.values()) {
//            if (type.getValue().equals(text)) {
//                return type;
//            }
//        }
//        throw new IllegalArgumentException("No constant with text " + text + " found");
//    }
}


