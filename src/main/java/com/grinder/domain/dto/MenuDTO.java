package com.grinder.domain.dto;

import com.grinder.domain.entity.Menu;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;

public class MenuDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class saveMenuRequest {
        private String cafeId;
        private String menuId;
        private String menuName;
        private String menuPrice;
        private String menuVolume;
        private String menuAllergy;
        private String menuDetails;
        private String menuType;
        private String menuIsLimited;
        private MultipartFile menuImage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class findAllMenuResponse {
        private String menuId;
        private String menuName;
        private String menuPrice;
        private String menuUpdate;
        private String menuVolume;
        private String menuAllergy;
        private String menuDetails;
        private String menuIsLimited;
        private String menuType;
        private String menuImage;

        public findAllMenuResponse(Menu menu, String ImageUrl) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            menuId = menu.getMenuId();
            menuName = menu.getName();
            menuPrice = menu.getPrice();
            menuUpdate = menu.getUpdatedAt().format(formatter);
            menuVolume = menu.getVolume();
            menuAllergy = menu.getAllergy();
            menuDetails = menu.getDetails();
            if (menu.getIsLimited()) menuIsLimited = "한정 메뉴";
            else menuIsLimited = "상시";
            menuType = menu.getMenuType().toString();
            menuImage = ImageUrl;
        }
    }
}
