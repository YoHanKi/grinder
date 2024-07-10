package com.grinder.service.implement;

import com.grinder.domain.dto.AlanDTO;
import com.grinder.domain.entity.AnalysisTag;
import com.grinder.domain.entity.Message;
import com.grinder.repository.MessageRepository;
import com.grinder.service.AnalysisTagService;
import com.grinder.utils.AlanAPI;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AlanQuestionServiceImplTest {
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private AlanAPI alanAPI;
    @InjectMocks
    private AlanQuestionServiceImpl alanQuestionService;
    @Mock
    private AnalysisTagService analysisTagService;

    @Test
    void anyQuestion() {
        AlanDTO.AlanResponse question = new AlanDTO.AlanResponse();
        question.setContent("Test answer");
        question.setActionSpeak("Speak");
        question.setActionName("Name");

        doReturn(question).when(alanAPI).anyQuestion(any(String.class));

        AlanDTO.AlanResponse result = alanQuestionService.anyQuestion("test question");

        assertThat(result).extracting("content").isEqualTo("Test answer");
        assertThat(result).extracting("actionName").isEqualTo("Name");
        assertThat(result).extracting("actionSpeak").isEqualTo("Speak");
    }

    @Test
    void recommendCafe() {
        AnalysisTag analysisTag = AnalysisTag.builder().tagList("test tagList").build();
        AlanDTO.AlanResponse question = new AlanDTO.AlanResponse();
        question.setContent("test question");
        Message message = Message.builder().content("ture").build();
        doReturn(analysisTag).when(analysisTagService).findByEmail(any(String.class));
        doReturn(question).when(alanAPI).recommendCafeByTag(any(String.class));
        doReturn(message).when(messageRepository).save(any(Message.class));

        boolean result = alanQuestionService.recommendCafe("test@test.com");

        assertThat(result).isTrue();
    }
}