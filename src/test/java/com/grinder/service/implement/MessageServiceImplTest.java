package com.grinder.service.implement;

import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Message;
import com.grinder.repository.MessageRepository;
import com.grinder.repository.queries.MemberQueryRepository;
import com.grinder.repository.queries.MessageQueryRepository;
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

    @Test
    void existNonCheckMessage() {
        doReturn(true).when(messageQueryRepository).existsNonCheck(anyString());

        boolean result = messageService.existNonCheckMessage("test");

        assertThat(result).isTrue();
    }

    @Test
    void findAllByEmail() {
        Message message = Message.builder().messageId("id").sendMember(Mockito.mock(Member.class)).receiveMember(Mockito.mock(Member.class)).content("content").isChecked(false).adminName("name").build();
        doReturn(List.of(message)).when(messageRepository).findAllByReceiveMember_Email(anyString());

        List<Message> result = messageService.findAllByEmail("test");

        assertThat(result).extracting("messageId").contains("id");
    }
}