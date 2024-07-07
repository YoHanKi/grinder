package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Message;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class MessageQueryRepositoryTest {
    @Autowired
    MessageQueryRepository messageQueryRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder().email("test@test.com").nickname("test").phoneNum("01012341234").password("1234").build());
        Member member1 = memberRepository.save(Member.builder().email("test1@test.com").nickname("test1").phoneNum("01012341234").password("1234").build());
        messageRepository.save(Message.builder().sendMember(member).receiveMember(member1).content("message").build());
    }

    @Test
    void existsNonCheck() {
        boolean result = messageQueryRepository.existsNonCheck("test@test.com");
        boolean result1 = messageQueryRepository.existsNonCheck("test1@test.com");

        assertFalse(result);
        assertTrue(result1);
    }
}