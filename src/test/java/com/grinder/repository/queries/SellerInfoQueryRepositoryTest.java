package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.dto.SellerInfoDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.SellerInfo;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.SellerInfoRepository;
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
class SellerInfoQueryRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private SellerInfoRepository sellerInfoRepository;
    @Autowired
    private SellerInfoQueryRepository sellerInfoQueryRepository;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder().email("test@test.com").nickname("test").phoneNum("01012341234").password("1234").build());
        Cafe cafe = cafeRepository.save(Cafe.builder().name("그라인더0").phoneNum("01012341234").address("서울시 강남구").build());
        sellerInfoRepository.save(SellerInfo.builder().member(member).cafe(cafe).build());
    }
    @Test
    void findAllSellerInfo() {
        List<SellerInfoDTO.findAllResponse> list = sellerInfoQueryRepository.findAllSellerInfo("test@test.com");

        assertThat(list).extracting("cafeName").contains("그라인더0");
    }
}