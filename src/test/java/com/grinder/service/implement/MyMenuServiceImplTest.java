package com.grinder.service.implement;

import com.grinder.domain.dto.MenuDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Image;
import com.grinder.domain.entity.Menu;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.MenuType;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.ImageRepository;
import com.grinder.repository.MenuRepository;
import com.grinder.repository.SellerInfoRepository;
import com.grinder.repository.queries.MenuQueryRepository;
import com.grinder.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MyMenuServiceImplTest {
    @Spy
    @InjectMocks
    MyMenuServiceImpl myMenuService;
    @Mock
    MenuRepository menuRepository;
    @Mock
    SellerInfoRepository sellerInfoRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    MenuQueryRepository menuQueryRepository;

    Cafe cafe1;
    Menu menu1;
    Image image1;
    @BeforeEach
    void setUp() {
        cafe1 = Cafe.builder().cafeId("testId").name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        menu1 = Menu.builder().menuId("menuId").name("name").cafe(cafe1).price("1").volume("2").allergy("aller").details("details").menuType(MenuType.BEVERAGE).isLimited(false).build();
        image1 = Image.builder().imageId("imageId").imageUrl("imageUrl").contentType(ContentType.FEED).contentId("contentId").build();
    }
    @Test
    void findAllMenuWithImage() {
        doReturn(true).when(sellerInfoRepository).existsByMember_EmailAndCafe_CafeId(anyString(), anyString());

        setUpdatedAt(menu1, LocalDateTime.now());

        List<MenuDTO.findAllMenuResponse> list = List.of(new MenuDTO.findAllMenuResponse(menu1, image1.getImageUrl()));
        doReturn(list).when(menuQueryRepository).findAllMenuWithImage(anyString());

        List<MenuDTO.findAllMenuResponse> result = myMenuService.findAllMenuWithImage("test", "test");

        assertThat(result).extracting("menuId").contains("menuId");
    }

    @Test
    void deleteMenu() {
        doNothing().when(menuRepository).deleteByMenuIdAndCafe_CafeId(anyString(), anyString());
        doReturn(true).when(imageRepository).existsAllByContentTypeAndContentId(any(ContentType.class), anyString());
        doReturn(Optional.of(image1)).when(imageRepository).findByContentTypeAndContentId(any(ContentType.class), anyString());
        doNothing().when(imageRepository).delete(any(Image.class));

        boolean result = myMenuService.deleteMenu("test", "test");

        assertThat(result).isTrue();
    }

    private void setUpdatedAt(Menu menu, LocalDateTime updatedAt) {
        try {
            java.lang.reflect.Field field = menu.getClass().getSuperclass().getDeclaredField("updatedAt");
            field.setAccessible(true);
            field.set(menu, updatedAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}