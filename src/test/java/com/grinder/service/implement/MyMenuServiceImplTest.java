package com.grinder.service.implement;

import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Image;
import com.grinder.domain.entity.Menu;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.MenuType;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.ImageRepository;
import com.grinder.repository.MenuRepository;
import com.grinder.repository.SellerInfoRepository;
import com.grinder.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
    }

    @Test
    void deleteMenu() {
    }
}