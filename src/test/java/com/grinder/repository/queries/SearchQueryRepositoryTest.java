package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.Member;
import com.grinder.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class SearchQueryRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SearchQueryRepository searchQueryRepository;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder().email("test@test.com").nickname("test").phoneNum("01012341234").password("1234").build());
        Member member1 = memberRepository.save(Member.builder().email("test1@test.com").nickname("test1").phoneNum("01012341234").password("1234").build());
    }

    @Test
    void searchMembersByNicknameOrEmail() {
        Slice<MemberDTO.FindMemberAndImageDTO> slice = searchQueryRepository.searchMembersByNicknameOrEmail("test", Pageable.ofSize(1));

        assertThat(slice.getSize()).isEqualTo(1);
        assertThat(slice).extracting("nickname").contains("test");
    }
}