package com.grinder.service.implement;

import com.grinder.domain.entity.AnalysisTag;
import com.grinder.exception.RecentAddedTagException;
import com.grinder.repository.AnalysisTagRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.AnalysisTagQueryRepository;
import com.grinder.service.AnalysisTagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AnalysisTagServiceImplTest {
    @InjectMocks
    private AnalysisTagServiceImpl analysisTagService;
    @Mock
    private AnalysisTagRepository analysisTagRepository;
    @Mock
    private AnalysisTagQueryRepository analysisTagQueryRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void updateTagList() {
        //기존 태그
        Optional<AnalysisTag> analysisTag = Optional.ofNullable(AnalysisTag.builder().tagList("tag").build());
        doReturn(analysisTag).when(analysisTagRepository).findByMember_Email(any(String.class));
        //분석 태그
        List<String> list = List.of("tag1","tag2");
        doReturn(list).when(analysisTagQueryRepository).AnalysisMemberTag(any(String.class));
        doReturn(AnalysisTag.builder().tagList("tag/tag1/tag2").build()).when(analysisTagRepository).save(any(AnalysisTag.class));

        analysisTagService.updateTagList("test");

        verify(analysisTagRepository, times(2)).save(analysisTag.get());
    }

    @Test
    void findByEmail_최근태그추가발생() {
        Optional<AnalysisTag> analysisTag = Optional.ofNullable(AnalysisTag.builder().tagList("tag").build());
        setUpdatedAt(analysisTag.get(), LocalDateTime.now().minusDays(2));
        doReturn(analysisTag).when(analysisTagRepository).findByMember_Email(any(String.class));

        assertThatThrownBy(() -> analysisTagService.findByEmail("test")).isInstanceOf(RecentAddedTagException.class).hasMessage("최근 태그가 추가 되었습니다. 다음에 다시 추가해주세요!");
    }

    @Test
    void findByEmail_성공() {
        Optional<AnalysisTag> analysisTag = Optional.ofNullable(AnalysisTag.builder().tagList("tag").build());
        setUpdatedAt(analysisTag.get(), LocalDateTime.now().minusDays(5));
        doReturn(analysisTag).when(analysisTagRepository).findByMember_Email(any(String.class));

        AnalysisTag result = analysisTagService.findByEmail("test");

        assertThat(result).extracting("tagList").isEqualTo("tag");
    }

    private void setUpdatedAt(AnalysisTag analysisTag, LocalDateTime updatedAt) {
        try {
            java.lang.reflect.Field field = analysisTag.getClass().getSuperclass().getDeclaredField("updatedAt");
            field.setAccessible(true);
            field.set(analysisTag, updatedAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findByEmail_예외() {
        doReturn(Optional.empty()).when(analysisTagRepository).findByMember_Email(any(String.class));
        doReturn(Optional.empty()).when(memberRepository).findByEmail(any(String.class));

        assertThatThrownBy(() -> analysisTagService.findByEmail("test")).isInstanceOf(IllegalArgumentException.class).hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void addTagList() {
        boolean result = analysisTagService.addTagList(new ArrayList<>(), AnalysisTag.builder().tagList("tag").build());

        assertThat(result).isTrue();
    }
}