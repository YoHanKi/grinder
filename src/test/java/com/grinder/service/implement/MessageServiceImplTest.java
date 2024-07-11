package com.grinder.service.implement;

import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Message;
import com.grinder.domain.enums.Role;
import com.grinder.repository.MessageRepository;
import com.grinder.repository.queries.MemberQueryRepository;
import com.grinder.repository.queries.MessageQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Spy
    @InjectMocks
    MessageServiceImpl messageService;
    @Mock
    MessageRepository messageRepository;
    @Mock
    MessageQueryRepository messageQueryRepository;

    Message message;
    Member member1;
    Member member2;

    @BeforeEach
    void setUp() {
        member1 = Member.builder().memberId("test").email("test@test.com").role(Role.MEMBER).nickname("testNick").password("test").phoneNum("1234").isDeleted(false).build();
        member2 = Member.builder().memberId("test1").email("test1@test.com").role(Role.ADMIN).nickname("testNick1").password("test").phoneNum("1234").isDeleted(false).build();
        message = Message.builder().messageId("id").sendMember(member1).receiveMember(member2).content("content").isChecked(false).adminName("admin").build();
    }

    @Test
    void existNonCheckMessage() {
        doReturn(true).when(messageQueryRepository).existsNonCheck(anyString());

        boolean result = messageService.existNonCheckMessage("test");

        assertThat(result).isTrue();
    }

    @Test
    void findAllByEmail() {
        doReturn(List.of(message)).when(messageRepository).findAllByReceiveMember_Email(anyString());

        List<Message> result = messageService.findAllByEmail("test");

        assertThat(result).extracting("messageId").contains("id");
        assertThat(result).extracting("sendMember").contains(member1);
        assertThat(result).extracting("receiveMember").contains(member2);
        assertThat(result).extracting("content").contains("content");
        assertThat(result).extracting("isChecked").contains(false);
        assertThat(result).extracting("adminName").contains("admin");
    }
}