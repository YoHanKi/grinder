package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.TagName;
import com.grinder.repository.*;
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
class ImageQueryRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private ImageQueryRepository imageQueryRepository;

    @BeforeEach
    public void setUp() {
        Cafe cafe = cafeRepository.save(Cafe.builder().name("그라인더0").phoneNum("01012341234").address("서울시 강남구").build());
        Image image = imageRepository.save(Image.builder().contentType(ContentType.CAFE).contentId(cafe.getCafeId()).imageUrl("1234").build());
    }

    @Test
    void findImageUrlByContentId() {
        Cafe cafe = cafeRepository.findAll().get(0);
        String imageUrl = imageQueryRepository.findImageUrlByContentId(cafe.getCafeId());

        assertThat(imageUrl).isEqualTo("1234");
    }
}