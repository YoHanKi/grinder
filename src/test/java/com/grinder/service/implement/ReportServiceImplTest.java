package com.grinder.service.implement;

import com.grinder.domain.dto.ReportDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Report;
import com.grinder.domain.enums.ContentType;
import com.grinder.repository.ReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @InjectMocks
    @Spy
    ReportServiceImpl reportService;
    @Mock
    ReportRepository reportRepository;
    @Mock
    CommentServiceImpl commentService;
    @Mock
    FeedServiceImpl feedService;

    @DisplayName("신고 내역 조회")
    @Test
    void testFindAllReports() {
        //given
        List<Report> reportList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            reportList.add(Report.builder().member(new Member()).contentType(ContentType.FEED).contentId(UUID.randomUUID().toString()).build());
        }
        Feed feed = Feed.builder().cafe(new Cafe()).member(new Member()).content("피드내용").build();

        doReturn(reportList).when(reportRepository).findAll();
        doReturn(feed).when(feedService).findFeed(anyString());

        //when
        List<ReportDTO.FindReportDTO> reportDTOList = reportService.findAllReports();

        //then
        assertThat(reportDTOList.size()).isEqualTo(2);
    }

    @DisplayName("신고 내역 삭제")
    @Test
    void testDeleteReport() {
        Report report = Report.builder().reportId(UUID.randomUUID().toString()).member(new Member()).build();

        doReturn(Optional.of(report)).when(reportRepository).findById(report.getReportId());
        doNothing().when(reportRepository).delete(report);

        reportService.deleteReport(report.getReportId());

        verify(reportRepository, times(1)).delete(report);
    }

    @DisplayName("신고된 컨텐츠 가리기")
    @Test
    void testDeleteContent() {
        //given
        String feedReportId = UUID.randomUUID().toString();

        Report feedReport = Report.builder().reportId(feedReportId).member(new Member()).contentType(ContentType.FEED).contentId(UUID.randomUUID().toString()).build();

        doNothing().when(feedService).deleteFeed(anyString());
        doReturn(feedReport).when(reportService).findReportById(anyString());
        doNothing().when(reportService).deleteAllReportByContentId(any(Report.class));

        //when
        reportService.deleteContent(feedReportId);

        //then
        verify(feedService, times(1)).deleteFeed(anyString());
        verify(commentService, times(0)).deleteComment(anyString());
        verify(reportService, times(1)).deleteAllReportByContentId(any(Report.class));
    }
}