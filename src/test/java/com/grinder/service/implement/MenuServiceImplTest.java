package com.grinder.service.implement;

import com.grinder.domain.dto.MenuDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.CafeSummary;
import com.grinder.domain.entity.Image;
import com.grinder.domain.entity.Menu;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.MenuType;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.MenuRepository;
import com.grinder.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {
    @Spy
    @InjectMocks
    MenuServiceImpl menuService;
    @Mock
    MenuRepository menuRepository;
    @Mock
    AwsS3ServiceImpl s3Service;
    @Mock
    CafeRepository cafeRepository;
    @Mock
    ImageService imageService;

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
    void findAllMenusByCafeId() {
        doReturn(List.of(menu1)).when(menuRepository).findAllByCafe_CafeId(anyString());
        doReturn("1234").when(imageService).findImageUrlByContentId(anyString());

        setUpdatedAt(menu1, LocalDateTime.now());

        List<MenuDTO.findAllMenuResponse> result = menuService.findAllMenusByCafeId("test");

        assertThat(result).extracting("menuId").contains(menu1.getMenuId());
        assertThat(result).extracting("menuName").contains(menu1.getName());
        assertThat(result).extracting("menuPrice").contains(menu1.getPrice());
        assertThat(result).extracting("menuUpdate").contains(result.get(0).getMenuUpdate());
        assertThat(result).extracting("menuVolume").contains(menu1.getVolume());
        assertThat(result).extracting("menuAllergy").contains(menu1.getAllergy());
        assertThat(result).extracting("menuDetails").contains(menu1.getDetails());
        assertThat(result).extracting("menuIsLimited").contains("상시");
        assertThat(result).extracting("menuType").contains(menu1.getMenuType().toString());
        assertThat(result).extracting("menuImage").contains("1234");
    }

    @Test
    void saveMyCafeMenu() {
        doReturn(image1).when(s3Service).uploadSingleImageBucket(any(MultipartFile.class), anyString(), any(ContentType.class));
        doReturn(Optional.of(cafe1)).when(cafeRepository).findById(anyString());
        doReturn(menu1).when(menuRepository).save(any(Menu.class));

        MenuDTO.saveMenuRequest request = new MenuDTO.saveMenuRequest();
        request.setCafeId("test");
        request.setMenuId("id");
        request.setMenuAllergy("al");
        request.setMenuDetails("de");
        request.setMenuName("na");
        request.setMenuPrice("1");
        request.setMenuVolume("2");
        request.setMenuType("커피");
        request.setMenuIsLimited("상시");
        request.setMenuImage(Mockito.mock(MultipartFile.class));

        boolean result = menuService.saveMyCafeMenu(request);

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