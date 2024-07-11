package com.grinder.service.implement;

import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.ContentType;
import com.grinder.repository.*;
import com.grinder.service.AlanQuestionService;
import com.grinder.service.AnalysisTagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceImplTest {

    @Spy
    @InjectMocks
    private SchedulerServiceImpl schedulerService;

    @Mock
    private CafeRepository cafeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AnalysisTagService analysisTagService;

    @Mock
    private AlanQuestionService alanQuestionService;

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private HeartRepository heartRepository;

    @Mock
    private CommentRepository commentRepository;

    private Member member;
    private Cafe cafe;
    private Feed feed;

    @BeforeEach
    void setUp() {
        member = Member.builder().memberId("testMemberId").email("member1@example.com").nickname("user1").build();
        cafe = Cafe.builder().cafeId("testCafeId").name("Cafe A").averageGrade(4).build();
        feed = Feed.builder().feedId("testFeedId").member(member).cafe(cafe).build();
    }

    @Test
    void testCalAverage() {
        doNothing().when(schedulerService).performCalAverageTask();
        schedulerService.CalAverage();
        verify(schedulerService, times(1)).performCalAverageTask();
    }

    @Test
    void testPerformCalAverageTask() {
        doNothing().when(schedulerService).updateAverageGradeForCafes(anyInt());
        doNothing().when(schedulerService).updateTagListForMembers(anyInt());
        doNothing().when(schedulerService).recommendCafeForMembers(anyInt());
        doNothing().when(schedulerService).updateRanks(anyInt());

        schedulerService.performCalAverageTask();

        verify(schedulerService, times(1)).updateAverageGradeForCafes(anyInt());
        verify(schedulerService, times(1)).updateTagListForMembers(anyInt());
        verify(schedulerService, times(1)).recommendCafeForMembers(anyInt());
        verify(schedulerService, times(1)).updateRanks(anyInt());
    }

    @Test
    void testUpdateAverageGradeForCafes() {
        when(cafeRepository.count()).thenReturn(100L);
        when(cafeRepository.findCafesForAverageCalculation(anyLong(), anyLong())).thenReturn(List.of(cafe));
        doNothing().when(schedulerService).calculateAndSetAverageGrade(any(Cafe.class));

        schedulerService.updateAverageGradeForCafes(1);

        verify(cafeRepository, times(1)).count();
        verify(cafeRepository, times(1)).findCafesForAverageCalculation(anyLong(), anyLong());
        verify(schedulerService, times(1)).calculateAndSetAverageGrade(any(Cafe.class));
    }

    @Test
    void testCalculateAndSetAverageGrade() {
        when(cafeRepository.findAverageGradeByCafeId(anyString())).thenReturn(4.5);

        schedulerService.calculateAndSetAverageGrade(cafe);

        assertThat(cafe.getAverageGrade()).isEqualTo(4);
        verify(cafeRepository, times(1)).save(any(Cafe.class));
    }

    @Test
    void testUpdateTagListForMembers() {
        when(memberRepository.count()).thenReturn(100L);
        when(memberRepository.findMembersForTagUpdate(anyLong(), anyLong())).thenReturn(List.of(member));
        doNothing().when(analysisTagService).updateTagList(anyString());

        schedulerService.updateTagListForMembers(1);

        verify(memberRepository, times(1)).count();
        verify(memberRepository, times(1)).findMembersForTagUpdate(anyLong(), anyLong());
        verify(analysisTagService, times(1)).updateTagList(anyString());
    }

    @Test
    void testRecommendAlan() {
        doNothing().when(schedulerService).performRecommendAlanTask();
        schedulerService.recommendAlan();
        verify(schedulerService, times(1)).performRecommendAlanTask();
    }

    @Test
    void testPerformRecommendAlanTask() {
        doNothing().when(schedulerService).recommendCafeForMembers(anyInt());
        doNothing().when(schedulerService).updateRanks(anyInt());

        schedulerService.performRecommendAlanTask();

        verify(schedulerService, times(1)).recommendCafeForMembers(anyInt());
        verify(schedulerService, times(1)).updateRanks(anyInt());
    }

    @Test
    void testRecommendCafeForMembers() {
        when(memberRepository.count()).thenReturn(100L);
        when(memberRepository.findMembersForTagUpdate(anyLong(), anyLong())).thenReturn(List.of(member));
        doReturn(true).when(alanQuestionService).recommendCafe(anyString());

        schedulerService.recommendCafeForMembers(1);

        verify(memberRepository, times(1)).count();
        verify(memberRepository, times(1)).findMembersForTagUpdate(anyLong(), anyLong());
        verify(alanQuestionService, times(1)).recommendCafe(anyString());
    }

    @Test
    void testUpdateRanks() {
        when(feedRepository.count()).thenReturn(100L);
        when(feedRepository.findFeedsForRankUpdate(anyLong(), anyLong())).thenReturn(List.of(feed));
        when(heartRepository.countByContentTypeAndContentId(any(ContentType.class), anyString())).thenReturn(50L);
        when(commentRepository.countByFeed(any(Feed.class))).thenReturn(20L);

        schedulerService.updateRanks(1);

        verify(feedRepository, times(1)).count();
        verify(feedRepository, times(1)).findFeedsForRankUpdate(anyLong(), anyLong());
        verify(heartRepository, times(1)).countByContentTypeAndContentId(any(ContentType.class), anyString());
        verify(commentRepository, times(1)).countByFeed(any(Feed.class));
        verify(feedRepository, times(1)).updateFeedRank(anyString(), anyInt());
    }

    @Test
    void testUpdateRank() {
        doNothing().when(schedulerService).performUpdateRankTask();
        schedulerService.updateRank();
        verify(schedulerService, times(1)).performUpdateRankTask();
    }

    @Test
    void testPerformUpdateRankTask() {
        doNothing().when(schedulerService).updateRanks(anyInt());
        schedulerService.performUpdateRankTask();
        verify(schedulerService, times(1)).updateRanks(anyInt());
    }

    @Test
    void testExecuteWithRetry() {
        doThrow(new RuntimeException("Test Exception")).doNothing().when(schedulerService).performCalAverageTask();

        schedulerService.executeWithRetry(schedulerService::performCalAverageTask, "CalAverage");

        verify(schedulerService, times(2)).performCalAverageTask(); // 1 실패 + 1 성공
    }
}