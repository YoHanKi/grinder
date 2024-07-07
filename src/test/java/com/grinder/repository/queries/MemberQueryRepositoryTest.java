package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.TagName;
import com.grinder.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class MemberQueryRepositoryTest {
    @Autowired
    MemberQueryRepository memberQueryRepository;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        Member member = memberRepository.save(Member.builder().email("test@test.com").nickname("test").phoneNum("01012341234").password("1234").build());
        Member member1 = memberRepository.save(Member.builder().email("test1@test.com").nickname("test1").phoneNum("01012341234").password("1234").build());
        Member member2 = memberRepository.save(Member.builder().email("test2@test.com").nickname("test2").phoneNum("01012341234").password("1234").build());
    }
    @Test
    void searchMemberByRoleAndNicknameSlice() {
        Slice<MemberDTO.FindMemberDTO> notRole = memberQueryRepository.searchMemberByRoleAndNicknameSlice("", "test", Pageable.ofSize(2));
        Slice<MemberDTO.FindMemberDTO> notNickname = memberQueryRepository.searchMemberByRoleAndNicknameSlice("MEMBER", "", Pageable.ofSize(3));
        Pageable pageable = PageRequest.of(1, 10);
        Slice<MemberDTO.FindMemberDTO> notPage = memberQueryRepository.searchMemberByRoleAndNicknameSlice("", "", pageable);

        // 검증 부분
        // notRole 검증
        assertThat(notRole).isNotNull();
        assertThat(notRole.hasNext()).isTrue(); // 페이지 크기 2, 결과 3개, 다음 페이지
        assertThat(notRole.getContent().size()).isEqualTo(2); // 결과 크기는 2

        // notNickname 검증
        assertThat(notNickname).isNotNull();
        assertThat(notNickname.hasNext()).isFalse(); // 페이지 크기 3, 결과 3개, 다음 페이지 없음
        assertThat(notNickname.getContent().size()).isEqualTo(3); // 결과 크기는 3

        // notPage 검증
        assertThat(notPage).isNotNull();
        assertThat(notPage.hasNext()).isFalse(); // 두 번째 페이지 요청, 데이터가 없으므로 다음 페이지 없음
        assertThat(notPage.getContent().size()).isEqualTo(0); // 두 번째 페이지에 해당하는 결과 없음
    }
}