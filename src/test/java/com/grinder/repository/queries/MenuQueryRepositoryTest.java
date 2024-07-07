package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.dto.MenuDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Menu;
import com.grinder.domain.enums.MenuType;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class MenuQueryRepositoryTest {
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private MenuQueryRepository menuQueryRepository;

    @BeforeEach
    public void setUp() {
        Cafe cafe = cafeRepository.save(Cafe.builder().name("그라인더0").phoneNum("01012341234").address("서울시 강남구").build());
        menuRepository.save(Menu.builder().name("아메리카노").menuType(MenuType.BEVERAGE).cafe(cafe).details("그냥").price("3000").isLimited(false).build());
    }

    @Test
    void findAllMenuWithImage() {
        String cafeId = cafeRepository.findAll().get(0).getCafeId();
        List<MenuDTO.findAllMenuResponse> list = menuQueryRepository.findAllMenuWithImage(cafeId);

        assertThat(list).isNotNull();
        assertThat(list).extracting("menuName").contains("아메리카노");
    }
}