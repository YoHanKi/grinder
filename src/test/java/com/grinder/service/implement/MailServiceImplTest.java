package com.grinder.service.implement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {
    @Spy
    @InjectMocks
    MailServiceImpl mailService;

    @Test
    void sendEmail() {
        mailService.sendEmail(anyString(),anyString(),anyString());

        mailService.sendEmail("test","test", "test");

        verify(mailService).sendEmail("test","test", "test");
    }
}